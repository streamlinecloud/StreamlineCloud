package net.streamlinecloud.main.command;

import net.streamlinecloud.main.StreamlineCloud;
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
                if (!Cache.i().getConfig().getWhitelist().isWhitelistEnabled()) {
                    Cache.i().getConfig().getWhitelist().setWhitelistEnabled(true);
                    StreamlineCloud.log("Whitelist enabled");
                    MainConfig.saveConfig();
                }
                break;

            case "disable":
                if (Cache.i().getConfig().getWhitelist().isWhitelistEnabled()) {
                    Cache.i().getConfig().getWhitelist().setWhitelistEnabled(false);
                    StreamlineCloud.log("Whitelist disabled");
                    MainConfig.saveConfig();
                }
                break;

            case "list":
                StreamlineCloud.log("Whitelist:");
                Cache.i().getConfig().getWhitelist().getWhitelist().forEach(user -> {
                    StreamlineCloud.log("- " + user);
                });
                break;

            case "add":
                Cache.i().getConfig().getWhitelist().getWhitelist().add(args[2]);
                StreamlineCloud.log("Added " + args[2] + " to the whitelist");
                MainConfig.saveConfig();
                break;

            case "remove":
                Cache.i().getConfig().getWhitelist().getWhitelist().remove(args[2]);
                StreamlineCloud.log("Removed " + args[2] + " from the whitelist");
                MainConfig.saveConfig();
                break;
        }
    }
}
