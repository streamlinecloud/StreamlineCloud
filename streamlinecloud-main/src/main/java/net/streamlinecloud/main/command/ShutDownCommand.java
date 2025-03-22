package net.streamlinecloud.main.command;

import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.terminal.api.CloudCommand;

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
