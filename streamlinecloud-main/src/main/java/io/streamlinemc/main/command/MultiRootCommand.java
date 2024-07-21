package io.streamlinemc.main.command;

import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.terminal.api.CloudCommand;
import io.streamlinemc.main.utils.Cache;

public class MultiRootCommand extends CloudCommand {

    public MultiRootCommand() {
        setName("multirroot");
        setAliases(new String[]{"mr", "cluster"});
        setDescription("Multiroot setup guide");
    }

    @Override
    public void execute(String[] args) {
        StreamlineCloud.log("sl.command.multiRoot.generate");
        StreamlineCloud.log("§GREENmultiroot setup localhost:" + Cache.i().getConfig().getCommunicationBridgePort() + ";" + Cache.i().getApiKey());
    }
}
