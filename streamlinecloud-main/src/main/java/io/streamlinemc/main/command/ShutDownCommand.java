package io.streamlinemc.main.command;

import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.terminal.api.CloudCommand;

public class ShutDownCommand extends CloudCommand {

    public ShutDownCommand() {
        setName("shutdown");
        setAliases(new String[]{"adminwars", "stop"});
        setDescription("Exit StreamlineCloud");
    }

    @Override
    public void execute(String[] args) {
        StreamlineCloud.shutDown();
    }
}
