package io.streamlinemc.main.command;

import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.lang.CloudLanguage;
import io.streamlinemc.main.terminal.api.CloudCommand;
import io.streamlinemc.main.utils.StaticCache;
import io.streamlinemc.main.utils.StreamlineConfig;

public class MultiRootCommand extends CloudCommand {

    public MultiRootCommand() {
        setName("multirroot");
        setAliases(new String[]{"mr", "cluster"});
        setDescription("Multiroot setup guide");
    }

    @Override
    public void execute(String[] args) {
        StreamlineCloud.log("sl.command.multiRoot.generate");
        StreamlineCloud.log("§GREENmultiroot setup localhost:" + StaticCache.getConfig().getCommunicationBridgePort() + ";" + StaticCache.getApiKey());
    }
}
