package io.streamlinemc.main.command;

import io.streamlinemc.api.StreamlineAPI;
import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.terminal.api.CloudCommand;
import io.streamlinemc.main.utils.BuildSettings;
import io.streamlinemc.main.utils.Cache;

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

        if (args.length == 1) {

            Cache.i().getConfig().getWhitelist().add(args[0]);
            StreamlineCloud.log("Added " + args[0] + " to the whitelist");

        }

        if (args[0].equals("enable")) {
            if (!Cache.i().getConfig().isWhitelistEnabled()) {
                Cache.i().getConfig().setWhitelistEnabled(true);
                StreamlineCloud.log("Whitelist enabled");
            }
        }

        if (args[0].equals("disable")) {
            if (Cache.i().getConfig().isWhitelistEnabled()) {
                Cache.i().getConfig().setWhitelistEnabled(false);
                StreamlineCloud.log("Whitelist disabled");
            }
        }

    }
}
