package net.streamlinecloud.main.command;

import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.terminal.command.StreamlineCommand;
import net.streamlinecloud.main.terminal.command.StreamlineCommandTree;
import net.streamlinecloud.main.terminal.command.StreamlineSubcommand;

public class ShutDownCommand extends StreamlineCommand {

    public ShutDownCommand() {
        super("shutdown", "Exit StreamlineCloud", new StreamlineCommandTree()
                .add(new StreamlineSubcommand("execute", ((variables, logger) -> {
                    StreamlineCloud.shutDown();
                })))
        );
        setAliases(new String[]{"adminwars", "stop"});
        setDefaultSubCommand("execute");
        setDescription("Exit StreamlineCloud");
    }

}
