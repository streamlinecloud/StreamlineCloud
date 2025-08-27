package net.streamlinecloud.main.command;

import net.streamlinecloud.main.CloudMain;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.server.CloudServer;
import net.streamlinecloud.main.core.server.CloudServerManager;
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

        CloudServer server = CloudServerManager.getInstance().getServerByName(args[1]);

        if (server == null) {
            StreamlineCloud.log("Server " + args[1] + " not found");
            return;
        }

        if (server.isOutput()) {
            server.disableScreen();
        } else {
            if (Cache.i().getCurrentScreenServerName() != null)
                CloudServerManager.getInstance().getServerByName(Cache.i().getCurrentScreenServerName()).disableScreen();
            server.enableScreen();
        }
    }

}
