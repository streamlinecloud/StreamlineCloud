package net.streamlinecloud.main.command;

import net.streamlinecloud.main.core.server.RunningServerManager;
import net.streamlinecloud.main.terminal.api.CloudCommand;
import net.streamlinecloud.main.utils.Cache;

public class ExitCommand extends CloudCommand {

    public ExitCommand() {
        setName("exit");
        setAliases(new String[]{});
        setDescription("Exit the current screen");
    }

    @Override
    public void execute(String[] args) {
        RunningServerManager.getInstance().getServerByName(Cache.i().getCurrentScreenServerName()).disableScreen();
    }
}
