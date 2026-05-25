package net.streamlinecloud.main.command;

import com.google.gson.Gson;
import net.streamlinecloud.api.group.StreamlineGroup;
import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.api.terminal.ReplacePaket;
import net.streamlinecloud.api.terminal.StreamlineLogger;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.group.CloudGroup;
import net.streamlinecloud.main.terminal.api.CloudCommand;
import net.streamlinecloud.main.terminal.command.StreamlineCommand;
import net.streamlinecloud.main.terminal.command.StreamlineCommandTree;
import net.streamlinecloud.main.terminal.command.StreamlineSubcommand;
import net.streamlinecloud.main.terminal.command.SubcommandCheckExecute;
import net.streamlinecloud.main.utils.Utils;

import java.lang.reflect.Field;
import java.util.*;

public class GroupsCommand extends StreamlineCommand {

    static List<String> classFields = Arrays.stream(StreamlineGroup.class.getDeclaredFields()).toList().stream().map(Field::getName).toList();

    public GroupsCommand() {

        super("groups", "Manage all groups", new StreamlineCommandTree()
                .add(new StreamlineSubcommand("list", ((variables, logger) -> {
                    logger.info("sc.command.groups.list.title");

                    for (StreamlineGroup g : StreamlineCloud.getGroupManager().getGroups()) {
                        logger.info(g.getName() + " - online: " + /*CloudGroupManager.getInstance().getGroupOnlineServers(g).size() + */ " - minOnline: " + g.getMinOnlineCount());
                    }

                }))).add(new StreamlineSubcommand("create %name %type:server/poroxy %software %static:true/false", ((variables, logger) -> {
                            boolean staticGroup = variables.get("static:true/false").equals("true");

                            String name = variables.get("name").toString();
                            String runtimeS = variables.get("type:server/poroxy").toString();
                            ServerRuntime runtime;

                            if (runtimeS.equalsIgnoreCase("SERVER")) {
                                runtime = ServerRuntime.SERVER;
                            } else if (runtimeS.equalsIgnoreCase("PROXY")) {
                                runtime = ServerRuntime.PROXY;

                            } else {
                                logger.info("sc.command.groups.crate.enterValidRuntime");
                                return;
                            }

                            StreamlineGroup group = new CloudGroup(
                                    name,
                                    1,
                                    new ArrayList<>(),
                                    runtime,
                                    variables.get("software").toString());
                            group.setStaticGroup(staticGroup);

                            if (StreamlineCloud.getGroupManager().create(group))
                                logger.info("sc.command.groups.create.created", new ReplacePaket[]{new ReplacePaket("%1", group.getName())});
                            else
                                logger.info("sc.command.groups.create.cantSave", new ReplacePaket[]{new ReplacePaket("%1", group.getName())});

                        }))

                ).addCompleter("group %name",
                        () -> StreamlineCloud.getGroupManager().getGroups().stream().map(StreamlineGroup::getName).toList()

                ).addCheck("group %name", ((variables, logger) -> {
                    StreamlineGroup group = StreamlineCloud.getGroupManager().getGroup(variables.get("name"));
                    if (group == null) {
                        logger.info("Group does not exist");
                        return abort();
                    }
                    return data("group", group);

                }))
                .add(new StreamlineSubcommand("group %name", ((variables, logger) -> {
                    StreamlineGroup group = (StreamlineGroup) variables.get("group");
                    HashMap<String, Object> fields = new HashMap<>();

                    for (Field field : Utils.getAllFields(group.getClass())) {
                        field.setAccessible(true);
                        Object value = null;
                        try {
                            value = field.get(group);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        fields.put(field.getName(), value);
                    }

                    StreamlineCloud.log("Information about " + group.getName());
                    fields.forEach((k, v) -> {
                        if (v instanceof List) StreamlineCloud.log(k + ": " + new Gson().toJson(v));
                        else StreamlineCloud.log(k + ": " + v);
                    });
                })))
                .add(new StreamlineSubcommand("group %name delete", ((variables, logger) -> {

                    StreamlineGroup group = StreamlineCloud.getGroupManager().getGroup(variables.get("name").toString());

                    StreamlineCloud.getGroupManager().delete(group);
                    StreamlineCloud.log("sc.command.groups.delete.deleted", new ReplacePaket[]{new ReplacePaket("%1", group.getName())});

                })))
                .addCompleter("group %name set %field", () -> classFields)
                .add(new StreamlineSubcommand("group %name set %field %value", ((variables, logger) -> {
                    StreamlineGroup group = StreamlineCloud.getGroupManager().getGroup(variables.get("name").toString());
                    String field = variables.get("field").toString();
                    String value = variables.get("value").toString();

                    if (field.equals("name") || field.equals("runtime") || field.equals("staticGroup")) {
                        logger.warning("You cannot update this field while StreamlineCloud is running. We recommend deleting the group and creating a new one.");
                        return;
                    }

                    try {
                        assert group != null;
                        Utils.setFieldsFromMap(group, Map.of(
                                field, value
                        ));
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        throw new RuntimeException(e);
                    } catch (NumberFormatException e) {
                        logger.warning("Please enter a valid number");
                    }

                    StreamlineCloud.getGroupManager().update(group);

                    logger.info("Set " + field + " to " + value);

                })))
                .add(new StreamlineSubcommand("group %name addTemplate %path", ((variables, logger) -> {
                    StreamlineGroup group = (StreamlineGroup) variables.get("group");
                    if (group.getTemplates().contains((String) variables.get("path"))){
                        logger.info("Template already added");
                        return;
                    }

                    group.getTemplates().add((String) variables.get("path"));

                    StreamlineCloud.getGroupManager().update(group);
                    StreamlineCloud.getGroupTemplatesManager().update();

                    logger.info("Template added");

                })))
                .add(new StreamlineSubcommand("group %name removeTemplate %path", ((variables, logger) -> {
                    StreamlineGroup group = (StreamlineGroup) variables.get("group");
                    boolean success = group.getTemplates().remove((String) variables.get("path"));

                    StreamlineCloud.getGroupManager().update(group);
                    StreamlineCloud.getGroupTemplatesManager().update();

                    if (success) logger.info("Template removed");
                    else logger.info("Template does not exist");

                }))));
        setDefaultSubCommand("list");
        setAliases(new String[]{"g"});
        addCompleter();
    }

}
