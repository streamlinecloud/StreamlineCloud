package io.streamlinemc.main.command;

import io.streamlinemc.main.CloudMain;
import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.terminal.api.CloudCommand;
import io.streamlinemc.main.utils.StaticCache;

import static io.streamlinemc.main.plugin.PluginManager.commandManager;

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
         CloudMain.getInstance().getCommandMap().forEach(command -> {
             StreamlineCloud.log("- " + command.name() + ": " + command.description());
         });

        commandManager.commands.values().forEach(command -> {
            StreamlineCloud.log("§GOLD- " + command.getName() + ": " + command.getDescription());
        });
    }
}
