package net.streamlinecloud.main.command;

import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.server.RunningServer;
import net.streamlinecloud.main.core.server.RunningServerManager;
import net.streamlinecloud.main.terminal.api.CloudCommand;
import net.streamlinecloud.main.utils.Cache;

public class ScreenCommand extends CloudCommand {

    public ScreenCommand() {
        setName("screen");
        setAliases(new String[]{});
        setDescription("Switch wo specific screens");
    }

    @Override
    public void execute(String[] args) {

        if (args.length != 2) {
            StreamlineCloud.log("Please specify a server name");
            return;
        }

        RunningServer server = RunningServerManager.getInstance().getServerByName(args[1]);

        if (server == null) {
            StreamlineCloud.log("Server " + args[1] + " not found");
            return;
        }

        if (server.isOutput()) {
            server.disableScreen();
        } else {
            if (Cache.i().getCurrentScreenServerName() != null)
                RunningServerManager.getInstance().getServerByName(Cache.i().getCurrentScreenServerName()).disableScreen();
            server.enableScreen();
        }
    }

}
