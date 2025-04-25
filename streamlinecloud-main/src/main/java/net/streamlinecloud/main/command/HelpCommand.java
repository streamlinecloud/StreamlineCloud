package net.streamlinecloud.main.command;

import net.streamlinecloud.main.CloudMain;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.terminal.api.CloudCommand;
import net.streamlinecloud.main.utils.Cache;

import static net.streamlinecloud.main.extension.ExtensionManager.commandManager;

public class HelpCommand extends CloudCommand {

    public HelpCommand() {
        setName("help");
        setDescription("See all commands");
    }

    @Override
    public void execute(String[] args) {

        /*
        addArgument(new CloudCommandArgument("servers")
                .addSubargument(new CloudCommandArgument("add"))
                .addSubargument(new CloudCommandArgument("delete")
                        .addSubargument(new CloudCommandArgument("-force")))
                .addSubargument(new CloudCommandArgument("restart")
                        .addSubargument(new CloudCommandArgument("-force"))));
         */

        if (args.length == 2) {
            if (args[1].equals("-toggleDebugMode") || args[1].equals("-tdm")) {
                if (Cache.i().isDebugMode()) {
                    Cache.i().setDebugMode(false);
                    StreamlineCloud.log("Disabled!");
                } else {
                    Cache.i().setDebugMode(true);
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
