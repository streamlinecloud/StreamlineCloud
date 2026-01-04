package net.streamlinecloud.main.command;

import com.google.gson.Gson;
import net.streamlinecloud.api.packet.WhitelistConfigurationPacket;
import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.api.socket.SocketMessage;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.config.MainConfig;
import net.streamlinecloud.main.core.server.RunningServerManager;
import net.streamlinecloud.main.setup.SetupQuestion;
import net.streamlinecloud.main.terminal.api.CloudCommand;
import net.streamlinecloud.main.utils.Cache;

public class WhitelistCommand extends CloudCommand {

    public WhitelistCommand() {
        setName("whitelist");
        setAliases(new String[]{"wl"});
        setDescription("Configure the whitelist");
    }

    @Override
    public void execute(String[] args) {

        if (args.length == 1) {
            StreamlineCloud.log("whitelist list");
            StreamlineCloud.log("whitelist enable");
            StreamlineCloud.log("whitelist disable");
            StreamlineCloud.log("whitelist add <name>");
            StreamlineCloud.log("whitelist remove <name>");
            StreamlineCloud.log("whitelist add-maintenance <name>");
            StreamlineCloud.log("whitelist remove-maintenance <name>");
            return;
        }

        WhitelistConfigurationPacket whitelist = Cache.i().getConfig().getWhitelist();

        switch (args[1]) {

            case "enable":
                if (!whitelist.isEnabled()) {
                    whitelist.setEnabled(true);
                    StreamlineCloud.log("Whitelist enabled");
                    MainConfig.saveConfig();
                }
                break;

            case "disable":
                if (whitelist.isEnabled()) {
                    whitelist.setEnabled(false);
                    StreamlineCloud.log("Whitelist disabled");
                    MainConfig.saveConfig();
                }
                break;

            case "list":
                StreamlineCloud.log("Whitelist:");
                whitelist.getWhitelistedPlayers().forEach(user -> {
                    StreamlineCloud.log("- " + user + (whitelist.getPrivilegedPlayers().contains(user) ? " (privileged)" : ""));
                });
                StreamlineCloud.log("");
                StreamlineCloud.log("Maintenance servers:");
                whitelist.getMaintenanceServers().forEach(sever -> {
                    StreamlineCloud.log("- " + sever);
                });
                return;

            case "add":
                whitelist.getWhitelistedPlayers().add(args[2]);
                new PrivilegedQuestion().start(result -> {
                    if (result.equals("yes")) {
                        whitelist.getPrivilegedPlayers().add(args[2]);
                        StreamlineCloud.log("Privileged the user");
                        update();
                    }
                });
                StreamlineCloud.log("Added " + args[2] + " to the whitelist");
                MainConfig.saveConfig();
                break;

            case "remove":
                whitelist.getWhitelistedPlayers().remove(args[2]);
                whitelist.getPrivilegedPlayers().remove(args[2]);
                StreamlineCloud.log("Removed " + args[2] + " from the whitelist");
                MainConfig.saveConfig();
                break;

            case "add-maintenance":
                whitelist.getMaintenanceServers().add(args[2]);
                StreamlineCloud.log("Added " + args[2] + " to the maintenance list");
                MainConfig.saveConfig();
                break;

            case "remove-maintenance":
                whitelist.getMaintenanceServers().remove(args[2]);
                StreamlineCloud.log("Removed " + args[2] + " from the maintenance list");
                MainConfig.saveConfig();
                break;

            default:
                return;
        }

        update();
    }

    public void update() {
        RunningServerManager.getInstance().getRunningServers().forEach(server -> {
            if (!server.getRuntime().equals(ServerRuntime.PROXY)) return;
            server.send(new SocketMessage(SocketMessage.SocketMessageType.WHITELIST_UPDATE, new Gson().toJson(Cache.i().getConfig().getWhitelist())));
        });
    }

    class PrivilegedQuestion extends SetupQuestion {

        public PrivilegedQuestion() {
            super(InputType.BOOLEAN, "Do you want to privilege the user? Privileged users can use the /server command.", output -> true);
        }

    }
}
