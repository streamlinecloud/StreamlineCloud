package net.streamlinecloud.main.command;

import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.terminal.api.CloudCommand;
import net.streamlinecloud.main.utils.Cache;

public class MultiRootCommand extends CloudCommand {

    public MultiRootCommand() {
        setName("multirroot");
        setAliases(new String[]{"mr", "cluster"});
        setDescription("Multiroot setup guide");
    }

    @Override
    public void execute(String[] args) {
        StreamlineCloud.log("sl.command.multiRoot.generate");
        StreamlineCloud.log("§GREENmultiroot setup localhost:" + Cache.i().getConfig().getNetwork().getBackendPort() + ";" + Cache.i().getApiKey());
    }
}
