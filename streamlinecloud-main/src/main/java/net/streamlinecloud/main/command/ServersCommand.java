package net.streamlinecloud.main.command;

import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.server.CloudServer;
import net.streamlinecloud.main.terminal.api.CloudCommand;
import net.streamlinecloud.main.utils.Cache;

import java.io.File;
import java.io.IOException;

public class ServersCommand extends CloudCommand {

    public ServersCommand() {
        setName("servers");
        setAliases(new String[]{"ser", "s"});
        setDescription("Manage current online servers");
    }

    @Override
    public void execute(String[] args) {

        if (args.length == 1) {
            sendHelp();
            return;
        }

        String sub = args[1];

        switch (sub) {
            case "server":

                if (args[2] != null) {

                    CloudServer server = StreamlineCloud.getServerByName(args[2]);

                    if (server != null) {

                        String serverSub = args[3];

                        switch (serverSub) {
                            case "screen":

                                if (server.isOutput()) {
                                    server.disableScreen();
                                } else {
                                    if (Cache.i().getCurrentScreenServerName() != null)
                                        StreamlineCloud.getServerByName(Cache.i().getCurrentScreenServerName()).disableScreen();
                                    server.enableScreen();
                                }

                                break;
                            case "stop":
                                server.stop();

                            case "kill":
                                server.disableScreen();
                                server.kill();

                                break;
                            case "command":
                            case "cmd":
                            case "c":

                                if (args.length >= 5) {

                                    StringBuilder sb = new StringBuilder();

                                    for (int i = 0; i <= args.length; i++) {

                                        if (i < 5) continue;

                                        sb.append(args[i - 1]).append(" ");
                                    }

                                    sb.deleteCharAt(sb.length() - 1);

                                    server.addCommand(sb.toString());

                                } else {
                                    StreamlineCloud.log("sl.command.servers.command.enterCommand");
                                }

                                break;
                        }

                    } else {
                        StreamlineCloud.log("sl.command.servers.serverNotFound");
                    }

                } else {
                    StreamlineCloud.log("sl.command.servers.enterServerName");
                }

                break;
            case "start":

                if (args.length == 3) {

                    StreamlineCloud.startServerByGroup(StreamlineCloud.getGroupByName(args [2]));

                }  else {
                    StreamlineCloud.log("sl.command.groups.enterGroup");
                }
                break;
            case "startnew":

                if (args.length == 3) {

                    CloudServer server = new CloudServer(args[2], ServerRuntime.SERVER);
                    File javaExec = new File(Cache.i().getConfig().getDefaultJavaPath());
                    try {
                        server.start(javaExec);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    sendHelp();
                }
                break;
            case "list":

                StreamlineCloud.log("Running servers:");
                for (CloudServer ser : Cache.i().getRunningServers()) {
                    StreamlineCloud.log(ser.getName() + "-" + ser.getUuid() + " | " + ser.getServerState() + " | PORT: " + ser.getPort() + " | GROUP: " + ser.getGroupDirect().getName());
                }

                break;
        }

        if (args[1].equals("help")) {
            StreamlineCloud.log("Start:");
            StreamlineCloud.log("- servers start <groupName>");
            StreamlineCloud.log("- servers startnew <name>");
            StreamlineCloud.log("Manage:");
            StreamlineCloud.log("- servers list");
            StreamlineCloud.log("- servers server <name> stop");
            StreamlineCloud.log("- servers server <name> kill");
            StreamlineCloud.log("- servers server <name> logs");
        }
    }

    public void sendHelp() {
        StreamlineCloud.log("Unknown Subcommand");
        StreamlineCloud.log("-> servers help");
    }
}
