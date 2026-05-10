package net.streamlinecloud.main.command;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.streamlinecloud.api.node.StreamlineNode;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.terminal.command.StreamlineCommand;
import net.streamlinecloud.main.terminal.command.StreamlineCommandTree;
import net.streamlinecloud.main.terminal.command.StreamlineSubcommand;

import java.lang.reflect.Type;
import java.util.List;

public class NodesCommand extends StreamlineCommand {

    public NodesCommand() {
        super(
                "nodes", "Manage all nodes connected to your cluster",
                new StreamlineCommandTree().add(
                        new StreamlineSubcommand("list", (vars, logger) -> {
                            for (StreamlineNode node : fetchNodes()) {
                                if (node.isWorker()) logger.info(
                                        "- " + node.getDisplayname()
                                                + " (" + node.getUuid() + ") - "
                                                + node.getStatus()
                                                + (node.isAdmin() ? " - ADMIN" : "")
                                );
                            }
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
        addCompleter();
        setDefaultSubCommand("list");
    }

    public static List<StreamlineNode> fetchNodes() {
        String json = StreamlineCloud.getHttpClient().fetchGetResponse("/nodes");
        Type type = new TypeToken<List<StreamlineNode>>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }

}