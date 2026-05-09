package net.streamlinecloud.main.command;

import net.streamlinecloud.main.terminal.command.StreamlineCommand;
import net.streamlinecloud.main.terminal.command.StreamlineCommandTree;
import net.streamlinecloud.main.terminal.command.StreamlineSubcommand;

public class NodesCommand extends StreamlineCommand {

    public NodesCommand() {
        super(
                "nodes",
                new StreamlineCommandTree().add(
                        new StreamlineSubcommand("list", (vars, logger) -> {
                            logger.info("Nodes list");
                        })
                ).add(
                        new StreamlineSubcommand("setName %name", (vars, logger) -> {
                            logger.info("Set name " + vars.get("name"));
                        })
                ).addCheck("admin %uuid", ((vars, logger) -> {
                    logger.info("Aborting not admin " + vars.get("uuid"));
                    return abort();
                })).add(
                        new StreamlineSubcommand("admin %uuid", (vars, logger) -> {
                            logger.info("admin");
                        })
                )
        );
    }

}