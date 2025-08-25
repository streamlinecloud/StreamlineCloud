package net.streamlinecloud.main.core.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.streamlinecloud.api.RestUtils.RconData;
import net.streamlinecloud.api.exception.TooManyAttemptsException;
import net.streamlinecloud.api.extension.event.server.*;
import net.streamlinecloud.api.group.StreamlineGroup;
import net.streamlinecloud.api.packet.StaticServerDataPacket;
import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.api.server.ServerState;
import net.streamlinecloud.api.server.StreamlineServer;
import net.streamlinecloud.api.server.StreamlineServerSerializer;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.backend.LoadBalancer;
import net.streamlinecloud.main.core.group.CloudGroup;
import net.streamlinecloud.main.core.group.CloudGroupManager;
import net.streamlinecloud.main.core.software.SoftwareManager;
import net.streamlinecloud.main.lang.ReplacePaket;
import net.streamlinecloud.main.utils.Cache;
import net.streamlinecloud.main.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;

import static net.streamlinecloud.main.extension.ExtensionManager.eventManager;

@Getter @Setter
public class CloudServer extends StreamlineServer {

    long startupTime = Calendar.getInstance().getTimeInMillis();
    List<String> logs = new ArrayList<>();
    Thread thread;
    Process process;
    List<String> commandQueue = new ArrayList<>();
    final String address = "localhost";
    File serverFolder;

    List<String> customTemplates = new ArrayList<>();

    boolean output = false;
    boolean staticServer = false;
    boolean isRestarting = false;

    public CloudServer(String name, ServerRuntime runtime) {
        setName(name);
        setRuntime(runtime);
        setUuid(String.valueOf(UUID.randomUUID()));

        CloudServerManager.getInstance().getServerRegister().put(getName(), this);

        ServerPreStartEvent serverPreStartEvent = eventManager.callEvent(new ServerPreStartEvent(name, runtime, getUuid(),ServerState.PREPARING));

        if (serverPreStartEvent.isCancelled()) {
            return;
        }

        CloudServerManager.getInstance().getRunningServers().add(this);
        setServerState(ServerState.PREPARING);
    }

    public String getShortUuid() {
        return getUuid().split("-")[0];
    }

    public void start(File javaExec) throws IOException {

        if (getGroup() == null)
            setGroup(Cache.i().getDefaultGroup().getName());

        if (getGroupDirect().getAutoRestartMinutes() != -1)
            setStopTime(System.currentTimeMillis() + getGroupDirect().getAutoRestartMinutes() * 60 * 1000L);

        if (getGroupDirect().getSoftwareName() == null) {
            StreamlineCloud.log("The group " + getGroup() + " does not have a software defined");
            return;
        }

        if (getGroupDirect().getSoftwareName().equals("default")) getGroupDirect().setSoftwareName(Cache.i().getConfig().getDefaultSoftwareName());

        if (SoftwareManager.getInstance().getSoftware(getGroupDirect().getSoftwareName()) == null) {
            StreamlineCloud.log("The software " + getGroupDirect().getSoftwareName() + " is not installed");
            return;
        }

        setStaticServer(getGroupDirect().isStaticGroup());
        setServerState(ServerState.STARTING);
        setPort(getFreePort());

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

        File file;
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
        Utils.runMkdir(file.mkdirs());

        serverFolder = file;

        //Eula
        try {
            File eula = new File(file.getAbsolutePath() + "/eula.txt");
            if (!eula.exists()) Files.createFile(eula.toPath());
            Files.writeString(eula.toPath(), "eula=true");
        } catch (IOException e) {
            StreamlineCloud.logError(e.getMessage());
        }

        //Copy Templates
        List<String> t = new ArrayList<>();
        CloudGroup g = CloudGroupManager.getInstance().getGroupByName(getGroup());

        assert g != null;
        customTemplates.addAll(g.getTemplates());

        t.add(Cache.i().homeFile.getPath() + "/templates/default/" + getRuntime().toString().toLowerCase());
        for (String s : customTemplates) t.add(Cache.i().homeFile.getPath() + "/templates/" + s);
        t.add(Cache.i().homeFile.getPath() + "/data/software/" + SoftwareManager.getInstance().getSoftware(getGroupDirect().getSoftwareName()).getFolder());
        Utils.copyFolder(t, file.getPath());

        File propertiesFile = new File(file.getAbsolutePath() + "/server.properties");
        Properties properties = new Properties();

        if (propertiesFile.exists()) properties.load(Files.newBufferedReader(Path.of(propertiesFile.toURI())));
        else Utils.runMkdir(propertiesFile.createNewFile());
        String rconpw = StreamlineCloud.generateApiKey();
        Cache.i().getRconDetails().put(getRconUuid(), new RconData(getIp(), getPort() + 1, rconpw));

        properties.setProperty("server-port", String.valueOf(getPort()));
        properties.setProperty("online-mode", String.valueOf(false));
        properties.setProperty("enforce-secure-profile", String.valueOf(false));
        properties.setProperty("enable-rcon", "true");
        properties.setProperty("rcon.port", String.valueOf(getPort() + 1));
        properties.setProperty("rcon.password", rconpw);

        properties.store(Files.newBufferedWriter(Path.of(propertiesFile.toURI())), null);

        Cache.i().rconDetails.put(getRconUuid(), new RconData(getIp(), getPort() + 1, rconpw));

        //Template From Resources
        if (getRuntime().equals(ServerRuntime.SERVER)) {
            Utils.copyResources(Utils.getResourceFile("spigot/spigot.yml", "yml"), new File(file.getAbsolutePath() + "/spigot.yml"));
        } else {
            Utils.copyResources(Utils.getResourceFile("bungee/config.yml", "yml"), new File(file.getAbsolutePath() + "/config.yml"));
        }

        File velocityFile = new File(file.getAbsolutePath() + "/velocity.toml");

        if (velocityFile.exists()) {
            String content = new String(Files.readAllBytes(velocityFile.toPath()));
            content = content.replace("%port", getPort() + "");
            Files.write(velocityFile.toPath(), content.getBytes());
        }

        //Apikey
        File f = new File(file.getPath() + "/.apikey");

        if (f.exists()) {
            FileUtils.forceDelete(f);
        }

        Files.createFile(f.toPath());
        FileUtils.writeStringToFile(f, Cache.i().getApiKey() + ",_," + Cache.i().getGson().toJson(new StaticServerDataPacket(getName(), getPort(), getIp(), getGroup(), getUuid(), getStopTime())), Charset.defaultCharset());

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
                        while (!getServerState().equals(ServerState.STOPPING)) {
                            if (!getProcess().isAlive()) {
                                delete();
                                return;
                            }

                            if ((line = inputReader.readLine()) != null) {
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
                        }
                    } catch (IOException ignored) {
                        return;
                    }

                    process.waitFor();

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
            return false;
        } catch (NullPointerException e) {
            StreamlineCloud.logError("We could not find the StreamlineCloudMC plugin");
            return false;
        }
        return true;
    }

    public void setOnline() {

        if (CloudServerManager.getInstance().getRestartingServers().containsKey(this)) {
            CloudServer oldServer = CloudServerManager.getInstance().getRestartingServers().get(this);
            Cache.i().getServerSocket().sendTo(oldServer, "move:" + getName());
        }

        String lb = null;

        for (LoadBalancer loadBalancer : Cache.i().getConfig().getNetwork().getLoadBalancers()) {
            if (loadBalancer.getGroup().equals(getGroup())) {
                loadBalancer.registerServer(this);
                lb = loadBalancer.getName();
            }
        }

        SoftwareManager.getInstance().copyCache(getGroupDirect().getSoftwareName(), this);

        if (lb == null) StreamlineCloud.log("sl.server.online", new ReplacePaket[]{new ReplacePaket("%1", getName() + "-" + getShortUuid())});
        else StreamlineCloud.log("sl.server.online.withLB", new ReplacePaket[]{new ReplacePaket("%1", getName() + "-" + getShortUuid()), new ReplacePaket("%2", lb)});
    }

    private int getFreePort()   {

        int attempts = 0;
        int port = -1;

        while (attempts < 10) {
            port = StreamlineCloud.generateUniquePort();
            if (StreamlineCloud.isPortAvailable(port)) return port;
            attempts++;
        }
        StreamlineCloud.logError("Failed to find a free port after " + attempts + " attempts. Using port: " + port);
        throw new TooManyAttemptsException(attempts, this.getClass());
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

        Cache.i().serverSocket.sendUpdate(this);

        ServerDeleteEvent serverDeleteEvent = eventManager.callEvent(new ServerDeleteEvent(getName(), getUuid()));

        if (serverDeleteEvent.isCancelled()) {
            return;
        }

        for (LoadBalancer loadBalancer : Cache.i().getConfig().getNetwork().getLoadBalancers()) {
            loadBalancer.getServers().stream().filter(server -> server.getUuid().equals(getUuid())).findFirst().ifPresent(server -> loadBalancer.getServers().remove(server));
        }

        CloudServerManager.getInstance().getRunningServers().remove(this);

        StreamlineCloud.log("sl.server.deleted", new ReplacePaket[]{new ReplacePaket("%1", getName() + "-" + getShortUuid())});
    }

    public void restart() {
        setRestarting(true);

        CloudServerManager serverManager = CloudServerManager.getInstance();
        CloudServer newServer = new CloudServer(getName(), getRuntime());
        serverManager.restartingServers.put(newServer, this);
        serverManager.getServersWaitingForStart().add(newServer);

        StreamlineCloud.log("Restarting " + getName());
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
        addCommand("stop");
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
        return CloudGroupManager.getInstance().getGroupByName(getGroup());
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

}
