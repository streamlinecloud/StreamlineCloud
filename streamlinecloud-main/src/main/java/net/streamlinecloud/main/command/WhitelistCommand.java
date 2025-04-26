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
            StreamlineCloud.log("whitelist enable");
            StreamlineCloud.log("whitelist disable");
            return;
        }

        if (args.length == 3) {

            Cache.i().getConfig().getWhitelist().getWhitelist().add(args[2]);
            StreamlineCloud.log("Added " + args[2] + " to the whitelist");
            MainConfig.saveConfig();

        }

        if (args[1].equals("enable")) {
            if (!Cache.i().getConfig().getWhitelist().isWhitelistEnabled()) {
                Cache.i().getConfig().getWhitelist().setWhitelistEnabled(true);
                StreamlineCloud.log("Whitelist enabled");
                MainConfig.saveConfig();
            }
        }

        if (args[1].equals("disable")) {
            if (Cache.i().getConfig().getWhitelist().isWhitelistEnabled()) {
                Cache.i().getConfig().getWhitelist().setWhitelistEnabled(false);
                StreamlineCloud.log("Whitelist disabled");
                MainConfig.saveConfig();
            }
        }

    }
}
