package io.streamlinemc.main.core.server;

import io.streamlinemc.api.server.*;
import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.core.group.CloudGroup;
import io.streamlinemc.main.lang.ReplacePaket;
import io.streamlinemc.main.utils.FileSystem;
import io.streamlinemc.main.utils.StaticCache;
import io.streamlinemc.main.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Getter @Setter
public class CloudServer extends StreamlineServer {

    long startupTime = Calendar.getInstance().getTimeInMillis();
    List<String> logs = new ArrayList<>();
    Thread thread;
    Process process;
    List<String> commandQueue = new ArrayList<>();

    boolean output = false;
    boolean staticServer = false;

    public CloudServer(String name, ServerRuntime runtime) {
        setName(name);
        setRuntime(runtime);
        setUuid(String.valueOf(UUID.randomUUID()));
        StaticCache.getRunningServers().add(this);
        setServerState(ServerState.PREPARING);

        StreamlineCloud.log("sl.server.preparing", new ReplacePaket[]{new ReplacePaket("%1", name)});
    }

    public void start(File javaExec) {

        if (getGroup() == null) {
            setGroup(StaticCache.getDefaultGroup().getName());
        }

        setStaticServer(getGroupDirect().isStaticGroup());

        setServerState(ServerState.STARTING);
        ServerTemplate template;

        StreamlineCloud.log("sl.server.starting", new ReplacePaket[]{
                new ReplacePaket("%1", getName() + "-" + getUuid()),
                new ReplacePaket("%2", "temp/" + getName())
        });

        setPort(StreamlineCloud.generateUniquePort());

        CloudGroup group = StreamlineCloud.getGroupByName(getGroup());

        if (group == null) {
            StreamlineCloud.logError("ServerStartError: Group is null!");
            return;
        }

        //CopyFiles
        if (isStaticServer()) {
            template = FileSystem.createStaticServer(this);

            //SetPort
            String filePath = FileSystem.homeFile + "/staticservers/" + getName() + "/server.properties";
            String searchTerm = "server-port=";
            String replacement = "server-port=" + getPort();

            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
                String line;
                StringBuilder fileContent = new StringBuilder();

                while ((line = bufferedReader.readLine()) != null) {
                    if (line.startsWith(searchTerm)) {
                        line = replacement/* + line.substring(searchTerm.length())*/;
                    }
                    fileContent.append(line).append("\n");
                }

                bufferedReader.close();

                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath));
                bufferedWriter.write(fileContent.toString());
                bufferedWriter.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            if (getGroup() == null) {
                template = FileSystem.createTemporaryServer(new ArrayList<>(), getName(), getUuid(), ServerRuntime.SERVER, this);
            } else {
                template = FileSystem.createTemporaryServer(group.getTemplates(), getName(), getUuid(), getRuntime(), this);
            }

            //SetPort
            try {
                File file = new File(template.getPath() + "/server.properties");

                FileWriter fileWriter = new FileWriter(file);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                bufferedWriter.write("server-port=" + getPort() + "\nonline-mode=false");
                //bufferedWriter.write("online-mode=false");

                bufferedWriter.close();
            } catch (IOException e) {
                System.out.println("error: " + e.getMessage());
            }
        }


        ScheduledExecutorService scheduler1 = Executors.newScheduledThreadPool(1);

        Runnable aufgabe = () -> {



            //StartServer
            AtomicInteger execcode = new AtomicInteger(-1);

            Thread jarThread = new Thread(() -> {
                try {
                    ProcessBuilder processBuilder = new ProcessBuilder(javaExec.getAbsolutePath(), "-jar", template.getServerJar().getAbsolutePath());
                    processBuilder.directory(template.getPath());
                    Process process = processBuilder.start();

                    this.process = process;

                    BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;

                    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                    scheduler.scheduleAtFixedRate(() -> {

                        if (commandQueue.size() != 0) {

                            String command = commandQueue.get(0);
                            commandQueue.remove(0);

                            executeCommand(command, process.getOutputStream());
                        }

                    }, 500, 500, TimeUnit.MILLISECONDS);


                    while ((line = inputReader.readLine()) != null) {

                        addLog(line);

                        if (output) {
                            StreamlineCloud.logSingle(getName() + line);
                        }
                    }

                    // Warte auf das Ende des Prozesses
                    execcode.set(process.waitFor());

                } catch (Exception e) {
                    StreamlineCloud.printError("CantStartServer", new String[]{"1", "2"}, e);
                }
                task();
            });

            jarThread.start();

            thread = jarThread;

            scheduler1.shutdown();
        };

        scheduler1.scheduleWithFixedDelay(aufgabe, 3, 3, TimeUnit.SECONDS);

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

    public void delete() {
        StaticCache.getRunningServers().remove(this);
        StaticCache.getLinkedServers().remove(this);

        StreamlineCloud.log("sl.server.deleted", new ReplacePaket[]{new ReplacePaket("%1", getName())});
    }

    public void kill() {
        if (isOutput()) disableScreen();

        if (process != null) {
            process.destroy();
            thread.interrupt();
        }
        delete();
    }

    public void enableScreen() {
        for (String log : getLogs()) {
            StreamlineCloud.logSingle(log);
        }
        setOutput(true);
        StaticCache.setCurrentScreenServerName(getName());
        StreamlineCloud.log("sl.server.screen.enabled", new ReplacePaket[]{new ReplacePaket("%1", getName())});
    }

    public void disableScreen() {
        setOutput(false);
        StaticCache.setCurrentScreenServerName(null);
        StreamlineCloud.log("sl.server.screen.disabled", new ReplacePaket[]{new ReplacePaket("%1", getName())});
    }

    public CloudGroup getGroupDirect() {
        return StreamlineCloud.getGroupByName(getGroup());
    }

    private void executeCommand(String command, OutputStream outputStream) {
        try {

            // Schreibe den Befehl in den OutputStream des Prozesses
            outputStream.write((command + System.lineSeparator()).getBytes());
            outputStream.flush();
            //CloudMain.getInstance().getTerminal().getRunner().run();

        } catch (IOException e) {
            System.out.println("Error executing command: " + e.getMessage());
        }
    }

    private void addLog(String str) {

        if (logs.size() > 300) {
            logs.remove(0);
        }
        logs.add(str);

    }

}
