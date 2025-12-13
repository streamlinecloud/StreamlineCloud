package net.streamlinecloud.main.command;

import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.server.RunningServerManager;
import net.streamlinecloud.main.setup.SetupQuestion;
import net.streamlinecloud.main.terminal.api.CloudCommand;
import net.streamlinecloud.main.utils.Cache;
import net.streamlinecloud.main.config.MainConfig;

public class WhitelistCommand extends CloudCommand {

    public WhitelistCommand() {
        setName("whitelist");
        setAliases(new String[]{"wl"});
        setDescription("Configure the whitelist");
    }

    @Override
    public void execute(String[] args) {

        if (args.length == 1) {
            StreamlineCloud.log("whitelist add <name>");
            StreamlineCloud.log("whitelist remove <name>");
            StreamlineCloud.log("whitelist enable");
            StreamlineCloud.log("whitelist disable");
            return;
        }

        switch (args[1]) {

            case "enable":
                if (!Cache.i().getConfig().getWhitelist().isEnabled()) {
                    Cache.i().getConfig().getWhitelist().setEnabled(true);
                    StreamlineCloud.log("Whitelist enabled");
                    MainConfig.saveConfig();
                }
                break;

            case "disable":
                if (Cache.i().getConfig().getWhitelist().isEnabled()) {
                    Cache.i().getConfig().getWhitelist().setEnabled(false);
                    StreamlineCloud.log("Whitelist disabled");
                    MainConfig.saveConfig();
                }
                break;

            case "list":
                StreamlineCloud.log("Whitelist:");
                Cache.i().getConfig().getWhitelist().getWhitelistedPlayers().forEach(user -> {
                    StreamlineCloud.log("- " + user + (Cache.i().getConfig().getWhitelist().getPrivilegedPlayers().contains(user) ? " (privileged)" : ""));
                });
                break;

            case "add":
                Cache.i().getConfig().getWhitelist().getWhitelistedPlayers().add(args[2]);
                new PrivilegedQuestion().start(result -> {
                    if (result.equals("yes")) Cache.i().getConfig().getWhitelist().getPrivilegedPlayers().add(args[2]);
                    StreamlineCloud.log("Privileged the user");
                });
                StreamlineCloud.log("Added " + args[2] + " to the whitelist");
                MainConfig.saveConfig();
                break;

            case "remove":
                Cache.i().getConfig().getWhitelist().getWhitelistedPlayers().remove(args[2]);
                Cache.i().getConfig().getWhitelist().getPrivilegedPlayers().remove(args[2]);
                StreamlineCloud.log("Removed " + args[2] + " from the whitelist");
                MainConfig.saveConfig();
                break;

            default:
                return;
        }

        RunningServerManager.getInstance().getRunningServers().forEach(server -> {
            if (server.getRuntime().equals(ServerRuntime.PROXY)) server.addCommand("refreshWhitelist");
        });
    }

    class PrivilegedQuestion extends SetupQuestion {

        public PrivilegedQuestion() {
            super(InputType.BOOLEAN, "Do you want to privilege the user? Privileged users can use the /server command.", output -> true);
        }

    }
}
