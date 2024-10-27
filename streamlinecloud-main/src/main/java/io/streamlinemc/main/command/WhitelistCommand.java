package io.streamlinemc.main.command;

import io.streamlinemc.api.StreamlineAPI;
import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.terminal.api.CloudCommand;
import io.streamlinemc.main.utils.BuildSettings;
import io.streamlinemc.main.utils.Cache;
import io.streamlinemc.main.utils.StreamlineConfig;

public class WhitelistCommand extends CloudCommand {

    public WhitelistCommand() {
        setName("whitelist");
        setAliases(new String[]{"wl"});
        setDescription("Configure the whitelist");
    }

    @Override
    public void execute(String[] args) {

        if (args.length == 0) {
            StreamlineCloud.log("whitelist add <name>");
            StreamlineCloud.log("whitelist enable");
            StreamlineCloud.log("whitelist disable");
            return;
        }

        if (args.length == 3) {

            Cache.i().getConfig().getWhitelist().add(args[2]);
            StreamlineCloud.log("Added " + args[2] + " to the whitelist");
            StreamlineConfig.saveConfig();

        }

        if (args[1].equals("enable")) {
            if (!Cache.i().getConfig().isWhitelistEnabled()) {
                Cache.i().getConfig().setWhitelistEnabled(true);
                StreamlineCloud.log("Whitelist enabled");
                StreamlineConfig.saveConfig();
            }
        }

        if (args[1].equals("disable")) {
            if (Cache.i().getConfig().isWhitelistEnabled()) {
                Cache.i().getConfig().setWhitelistEnabled(false);
                StreamlineCloud.log("Whitelist disabled");
                StreamlineConfig.saveConfig();
            }
        }

    }
}
