package net.streamlinecloud.main.command;

import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.group.CloudGroup;
import net.streamlinecloud.main.core.group.CloudGroupManager;
import net.streamlinecloud.main.core.server.CloudServer;
import net.streamlinecloud.main.core.server.CloudServerManager;
import net.streamlinecloud.main.terminal.api.CloudCommand;
import net.streamlinecloud.main.utils.Cache;

import java.io.File;
import java.io.IOException;

public class ServersCommand extends CloudCommand {

    public ServersCommand() {
        setName("servers");
        setAliases(new String[]{"s"});
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
            case "start":

                if (args.length == 3) {

                    CloudGroup group = CloudGroupManager.getInstance().getGroupByName(args[2]);

                    if (group == null) {
                        StreamlineCloud.log("The group " + args[2] + " doesn't exist. Starting a new server with the default template named " + args[2] + "...");

                        CloudServer server = new CloudServer(args[2], ServerRuntime.SERVER);
                        File javaExec = new File(Cache.i().getConfig().getDefaultJavaPath());
                        try {
                            server.start(javaExec);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        return;
                    }

                    CloudServerManager.getInstance().startServerByGroup(group);

                }  else {
                    StreamlineCloud.log("sl.command.server.start.enterName");
                }
                break;
            case "list":

                StreamlineCloud.log("Running servers:");
                for (CloudServer ser : CloudServerManager.getInstance().getRunningServers()) {
                    StreamlineCloud.log(ser.getName() + "-" + ser.getUuid() + " | " + ser.getServerState() + " - " + ser.getOnlinePlayers().size() + "/" + ser.getMaxOnlineCount() + " | PORT: " + ser.getPort() + " | GROUP: " + ser.getGroupDirect().getName());
                }
                break;

            default:
                CloudServer server = CloudServerManager.getInstance().getServerByName(args[1]);

                if (server != null) {

                    String serverSub = args[2];

                    switch (serverSub) {
                        case "stop":
                            server.stop();
                            break;

                        case "kill":
                            server.disableScreen();
                            server.kill();
                            break;

                        case "restart":
                            server.restart();
                            break;

                        case "command":
                        case "cmd":
                        case "c":

                            if (args.length >= 4) {

                                StringBuilder sb = new StringBuilder();

                                for (int i = 0; i <= args.length; i++) {

                                    if (i < 4) continue;

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
                break;
        }

        if (args[1].equals("help")) {
            StreamlineCloud.log("Start:");
            StreamlineCloud.log("- servers start <groupName/name>");
            StreamlineCloud.log("Manage:");
            StreamlineCloud.log("- servers list");
            StreamlineCloud.log("- servers <name> stop");
            StreamlineCloud.log("- servers <name> kill");
            StreamlineCloud.log("- servers <name> restart");
        }
    }

    public void sendHelp() {
        StreamlineCloud.log("Unknown Subcommand");
        StreamlineCloud.log("-> servers help");
    }
}
