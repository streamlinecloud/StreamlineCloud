package io.streamlinemc.main.command;

import io.streamlinemc.main.CloudMain;
import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.terminal.api.CloudCommand;
import io.streamlinemc.main.utils.StaticCache;

public class HelpCommand extends CloudCommand {

    public HelpCommand() {
        setName("help");
        setDescription("See all commands");
    }

    @Override
    public void execute(String[] args) {

        if (args.length == 2) {
            if (args[1].equals("-toggleDebugMode") || args[1].equals("-tdm")) {
                if (StaticCache.isDebugMode()) {
                    StaticCache.setDebugMode(false);
                    StreamlineCloud.log("Disabled!");
                } else {
                    StaticCache.setDebugMode(true);
                    StreamlineCloud.log("Have fun!");
                }
                return;
            }
        }

        StreamlineCloud.log("Commands:");
        for (CloudCommand command : CloudMain.getInstance().getCommandMap()) {
            StreamlineCloud.log("- " + command.name() + ": " + command.description());
        }
    }
}
