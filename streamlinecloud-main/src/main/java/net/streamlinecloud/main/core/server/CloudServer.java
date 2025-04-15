package net.streamlinecloud.main.core.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.streamlinemc.api.RestUtils.RconData;
import net.streamlinecloud.api.group.StreamlineGroup;
import net.streamlinecloud.api.packet.StaticServerDataPacket;
import net.streamlinecloud.api.plugin.event.predefined.*;
import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.api.server.ServerState;
import net.streamlinecloud.api.server.StreamlineServer;
import net.streamlinecloud.api.server.StreamlineServerSerializer;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.group.CloudGroup;
import net.streamlinecloud.main.lang.ReplacePaket;
import net.streamlinecloud.main.utils.Cache;
import net.streamlinecloud.main.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.*;

import static net.streamlinecloud.main.plugin.PluginManager.eventManager;

@Getter @Setter
public class CloudServer extends StreamlineServer {

    long startupTime = Calendar.getInstance().getTimeInMillis();
    List<String> logs = new ArrayList<>();
    Thread thread;
    Process process;
    List<String> commandQueue = new ArrayList<>();
    final String address = "localhost";

    List<String> customTemplates = new ArrayList<>();

    boolean output = false;
    boolean staticServer = false;

    public CloudServer(String name, ServerRuntime runtime) {
        setName(name);
        setRuntime(runtime);
        setUuid(String.valueOf(UUID.randomUUID()));

        ServerPreStartEvent serverPreStartEvent = eventManager.callEvent(new ServerPreStartEvent(name, runtime, getUuid(),ServerState.PREPARING));

        if (serverPreStartEvent.isCancelled()) {
            return;
        }

        Cache.i().getRunningServers().add(this);
        setServerState(ServerState.PREPARING);
    }

    public String getShortUuid() {
        return getUuid().split("-")[0];
    }

    public void start(File javaExec) throws IOException {

        if (getGroup() == null) setGroup(Cache.i().getDefaultGroup().getName());

        setStaticServer(getGroupDirect().isStaticGroup());
        setServerState(ServerState.STARTING);
        setPort(StreamlineCloud.generateUniquePort());

        if (!isStaticServer()) {

            StreamlineCloud.log("sl.server.starting", new ReplacePaket[]{
                    new ReplacePaket("%1", getName() + "-" + getShortUuid()),
                    new ReplacePaket("%2", "temp/" + getName())
            });
        } else {
            StreamlineCloud.log("sl.server.starting", new ReplacePaket[]{
                    new ReplacePaket("%1", getName()),
                    new ReplacePaket("%2", "staticservers/" + getShortUuid())
            });
        }

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(CloudServer.class, new StreamlineServerSerializer())
                .create();

        for (String session : Cache.i().getServerSocket().subscribedStartingServers.keySet()) {
            for (StreamlineGroup group : Cache.i().getServerSocket().subscribedStartingServers.get(session)) {
                if (group.getName().equals(getGroup())) {
                    Cache.i().getServerSocket().sessionMap.get(session).send(gson.toJson(this));
                }
            }
        }

        ServerTemplate template;
        File file = null;
        CloudGroup group = getGroupDirect();
        ServerStartEvent serverStartEvent = eventManager.callEvent(new ServerStartEvent(
                getName(),
                getUuid(),
                getGroup(),
                getServerState(),
                isStaticServer(),
                getPort()));

        if (serverStartEvent.isCancelled()) return;

        file = isStaticServer() ? new File(Cache.i().homeFile + "/staticservers/" + getName()) : new File(Cache.i().homeFile + "/temp/" + getName() + "-" + getShortUuid());
        file.mkdirs();

        File propertiesFile = new File(file.getAbsolutePath() + "/server.properties");

        //Properties
        if (!propertiesFile.exists()) {

            //Properties creation
            Properties properties = new Properties();
            try {

                propertiesFile.createNewFile();

                //String[][] replacements = {{"server-port=", "server-port=" + getPort()}, {"enable-rcon=false", "enable-rcon=true"}, {"rcon.port=25575", "rcon.port=" + (getPort() + 1)}, {"rcon.password=", "rcon.password=" + rconpw}};

                properties.load(Files.newBufferedReader(Path.of(propertiesFile.toURI())));

                String rconpw = StreamlineCloud.generatePassword();
                Cache.i().getRconDetails().put(getRconUuid(), new RconData(getIp(), getPort() + 1, rconpw));

                properties.setProperty("server-port", String.valueOf(getPort()));
                properties.setProperty("online-mode", String.valueOf(false));
                properties.setProperty("enforce-secure-profile", String.valueOf(false));
                properties.setProperty("enable-rcon", "true");
                properties.setProperty("rcon.port", String.valueOf(getPort() + 1));
                properties.setProperty("rcon.password", rconpw);
                properties.setProperty("max-players", String.valueOf(group.getServerOnlineCount()));

                properties.store(Files.newBufferedWriter(Path.of(propertiesFile.toURI())), null);

                Cache.i().rconDetails.put(getRconUuid(), new RconData(getIp(), getPort() + 1, rconpw));
            } catch (IOException e) {
                StreamlineCloud.logError(e.getMessage());
            }

            //Eula
            try {
                File eula = new File(file.getAbsolutePath() + "/eula.txt");
                if (!eula.exists()) eula.createNewFile();
                FileUtils.writeStringToFile(eula, "eula=true");
            } catch (IOException e) {
                StreamlineCloud.logError(e.getMessage());
            }

            //Copy Templates
            List<String> t = new ArrayList<>();
            CloudGroup g = StreamlineCloud.getGroupByName(getGroup());

            assert g != null;
            customTemplates.addAll(g.getTemplates());

            t.add(Cache.i().homeFile.getPath() + "/templates/default/" + getRuntime().toString().toLowerCase());
            for (String s : customTemplates) t.add(Cache.i().homeFile.getPath() + "/templates/" + s);
            copyFolder(t, file.getPath());

            //Template From Resources
            if (getRuntime().equals(ServerRuntime.SERVER)) {
                copyResources(Utils.getResourceFile("spigot/spigot.yml", "yml"), new File(file.getAbsolutePath() + "/spigot.yml"));
            } else {
                copyResources(Utils.getResourceFile("bungee/config.yml", "yml"), new File(file.getAbsolutePath() + "/config.yml"));
            }

        } else {
            if (isStaticServer()) {
                Properties properties = new Properties();
                properties.load(Files.newBufferedReader(Path.of(propertiesFile.toURI())));
                setPort(Integer.parseInt(properties.getProperty("server-port")));
            }
        }

        File velocityFile = new File(file.getAbsolutePath() + "/velocity.toml");

        if (velocityFile.exists()) {
            String content = new String(Files.readAllBytes(velocityFile.toPath()));
            content = content.replace("%port", getPort() + "");
            Files.write(velocityFile.toPath(), content.getBytes());
        }

        //Apikey
        File f = new File(file.getPath() + "/.apikey.json");

        if (f.exists()) {
            FileUtils.forceDelete(f);
        }

        f.createNewFile();
        FileUtils.writeStringToFile(f, Cache.i().getApiKey() + ",_," + Cache.i().getGson().toJson(new StaticServerDataPacket(getName(), getPort(), getIp(), getGroup(), getUuid())), Charset.defaultCharset());

        if (!new File(file.getPath() + "/server.jar").exists()) {

            StreamlineCloud.log("sl.server.jarNotFound", new ReplacePaket[]{
                    new ReplacePaket("%0", getName()),
                    new ReplacePaket("%1", getGroup()),
            });

            Cache.i().getDataCache().add("blacklistGroup:" + getGroup());

            kill();
            return;
        }

        if (!deployPlugin()) {
            StreamlineCloud.logError("Failed to deploy plugin: " + file.getAbsolutePath());
            return;
        }

        ScheduledExecutorService scheduler1 = Executors.newScheduledThreadPool(1);
        File finalFile = file;
        Runnable runnable = () -> {

            Thread jarThread = new Thread(() -> {
                try {
                    ProcessBuilder processBuilder = new ProcessBuilder(javaExec.getAbsolutePath(), "-jar", finalFile + "/server.jar", "nogui");
                    processBuilder.redirectErrorStream(true); // Combine stderr and stdout
                    processBuilder.directory(finalFile);
                    Process process = processBuilder.start();
                    this.process = process;

                    BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

                    // Command scheduler
                    scheduler.scheduleAtFixedRate(() -> {
                        if (!commandQueue.isEmpty()) {
                            String command = commandQueue.remove(0);
                            OutgoingServerMessageEvent outgoingEvent = eventManager.callEvent(
                                    new OutgoingServerMessageEvent(getName(), getUuid(), getGroup(), getServerState(), isStaticServer(), getPort(), command)
                            );
                            if (!outgoingEvent.isCancelled()) {
                                executeCommand(command, process.getOutputStream());
                            }
                        }
                    }, 500, 500, TimeUnit.MILLISECONDS);

                    String line;
                    try {
                        while (!getServerState().equals(ServerState.STOPPING) && (line = inputReader.readLine()) != null) {
                            addLog(line);
                            if (output) {
                                IncommingServerMessageEvent incommingEvent = eventManager.callEvent(
                                        new IncommingServerMessageEvent(getName(), getUuid(), getGroup(), getServerState(), isStaticServer(), getPort(), line)
                                );
                                if (!incommingEvent.isCancelled()) {
                                    StreamlineCloud.logSingle(getName() + " " + line);
                                }
                            }
                        }
                    } catch (IOException ignored) {
                        return;
                    }

                    if (getServerState().equals(ServerState.STOPPING)) process.waitFor();

                } catch (Exception e) {
                    if (!getServerState().equals(ServerState.STOPPING)) StreamlineCloud.printError("Failed to start server", e);
                } finally {
                    task();
                }
            });

            jarThread.start();
            thread = jarThread;
            scheduler1.shutdown();

        };

        scheduler1.scheduleWithFixedDelay(runnable, 3, 3, TimeUnit.SECONDS);

    }

    public boolean deployPlugin() {
        String pluginFileName = "streamlinecloud_MC-alpha-1.0.0";
        try {
            new File(Cache.i().homeFile + "/temp/" + getName() + "-" + getShortUuid() + "/plugins").mkdirs();
            Files.copy(Objects.requireNonNull(Utils.getResourceFile(pluginFileName, "")).toPath(), new File(Cache.i().homeFile + "/temp/" + getName() + "-" + getShortUuid() + "/plugins/streamlinecloud-mc.jar").toPath());
        } catch (IOException e) {
            StreamlineCloud.logError(e.getMessage());
        }
        return true;
    }

    public void addCommand(String command) {
        commandQueue.add(command);
    }

    public void task() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable aufgabe = () -> {

            if (!Utils.isProcessRunning(getProcess()) && !getServerState().equals(ServerState.ONLINE)) {
                kill();
                scheduler.shutdown();
            }
        };
        scheduler.scheduleWithFixedDelay(aufgabe, 3, 3, TimeUnit.SECONDS);
    }

    private void delete() {

        ServerDeleteEvent serverDeleteEvent = eventManager.callEvent(new ServerDeleteEvent(getName(), getUuid()));

        if (serverDeleteEvent.isCancelled()) {
            return;
        }

        Cache.i().getRunningServers().remove(this);

        StreamlineCloud.log("sl.server.deleted", new ReplacePaket[]{new ReplacePaket("%1", getName() + "-" + getShortUuid())});
    }

    private void copyResources(File sourceFile, File targetFile) {
        try {
            if (!targetFile.exists()) targetFile.createNewFile();

            YamlFile tempYamlFile = new YamlFile(sourceFile);
            YamlFile serverYamlFile = new YamlFile(targetFile);
            tempYamlFile.load();
            serverYamlFile.load();

            for (String key : tempYamlFile.getKeys(true)) {
                Object value = tempYamlFile.get(key);
                if (!serverYamlFile.contains(key)) {
                    serverYamlFile.set(key, value);
                }
            }

            serverYamlFile.save();
            tempYamlFile.deleteFile();


        } catch (IOException e) {
            StreamlineCloud.logError(e.getMessage());
        }
    }

    public void kill() {

        setServerState(ServerState.STOPPING);

        ServerStopEvent serverStopEvent = eventManager.callEvent(new ServerStopEvent(getName(), getUuid(), getGroup(), getServerState(), isStaticServer(), getPort()));

        if (serverStopEvent.isCancelled()) {
            return;
        }

        if (thread != null) thread.interrupt();

        if (isOutput()) disableScreen();

        if (process != null) {
            process.destroyForcibly();
        }
        delete();
    }

    public void stop() {
        setServerState(ServerState.STOPPING);
        executeCommand("stop", this.process.getOutputStream());
    }

    public void enableScreen() {
        for (String log : getLogs()) {
            StreamlineCloud.logSingle(log);
        }
        setOutput(true);
        Cache.i().setCurrentScreenServerName(getName());
        StreamlineCloud.log("sl.server.screen.enabled", new ReplacePaket[]{new ReplacePaket("%1", getName())});
    }

    public void disableScreen() {
        setOutput(false);
        Cache.i().setCurrentScreenServerName(null);
        StreamlineCloud.log("sl.server.screen.disabled", new ReplacePaket[]{new ReplacePaket("%1", getName())});
    }

    public CloudGroup getGroupDirect() {
        return StreamlineCloud.getGroupByName(getGroup());
    }

    private void executeCommand(String command, OutputStream outputStream) {
        try {

            outputStream.write((command + System.lineSeparator()).getBytes());
            outputStream.flush();

        } catch (IOException e) {
            StreamlineCloud.log("Error executing command: " + e.getMessage());
        }
    }

    private void addLog(String str) {

        if (logs.size() > 300) {
            logs.removeFirst();
        }
        logs.add(str);

    }

    private void copyFolder(List<String> folderPaths, String targetFolder) {

        Set<String> copiedFiles = new HashSet<>();

        for (String folderPath : folderPaths) {
            try {
                Path source = Paths.get(folderPath);
                Path destination = Paths.get(targetFolder);

                Files.walk(source)
                        .forEach(sourcePath -> {
                            Path relativePath = source.relativize(sourcePath);
                            Path destinationPath = destination.resolve(relativePath);

                            if (Files.isDirectory(sourcePath)) {
                                try {
                                    Files.createDirectories(destinationPath);
                                } catch (IOException e) {
                                    StreamlineCloud.logError(e.getMessage());
                                }
                            } else {
                                if (!copiedFiles.contains(destinationPath.toString())) {
                                    try {
                                        Files.createDirectories(destinationPath.getParent());
                                        Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                                        copiedFiles.add(destinationPath.toString());
                                    } catch (IOException e) {
                                        StreamlineCloud.logError(e.getMessage());
                                    }
                                }
                            }
                        });
            } catch (IOException e) {
                StreamlineCloud.logError(e.getMessage());
            }

        }

    }

}
