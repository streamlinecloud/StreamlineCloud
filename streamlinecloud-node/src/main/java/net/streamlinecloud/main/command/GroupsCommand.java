package net.streamlinecloud.main.command;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GroupsCommand extends StreamlineCommand {

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

                })).add(new StreamlineSubcommand("group %name", ((variables, logger) -> {
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
                    fields.forEach((k, v) -> StreamlineCloud.log(k + ": " + v));
                }))).add(new StreamlineSubcommand("group %name delete", ((variables, logger) -> {

                    StreamlineGroup group = StreamlineCloud.getGroupManager().getGroup(variables.get("name").toString());

                    if (group != null) {

                        StreamlineCloud.getGroupManager().delete(group);
                        StreamlineCloud.log("sc.command.groups.delete.deleted", new ReplacePaket[]{new ReplacePaket("%1", group.getName())});

                    } else {

                        StreamlineCloud.log("sc.command.groups.notFound", new ReplacePaket[]{new ReplacePaket("%1", variables.get("name").toString())});

                    }

                }))));
        setDefaultSubCommand("list");
        setAliases(new String[]{"g"});
        addCompleter();
    }

    public void execute(String[] args) {

        if (args.length == 1) {
            sendHelp();
            return;
        }

        String sub = args[1];

        switch (sub) {
            case "create":

                break;
            case "delete":

                break;
            case "list":

                break;
            case "group":

                if (args.length == 2) {
                    StreamlineCloud.log("sc.command.groups.enterGroup");
                    return;
                }

                if (args[2] != null) {

                    StreamlineGroup group = StreamlineCloud.getGroupManager().getGroup(args[2]);

                    if (group != null) {



                        String groupSub = args[3];

                        switch (groupSub) {
                            case "set" -> {

                                String setSub = args[4];

                                switch (setSub) {
                                    case "templates":
                                    case "name":
                                        break;

                                    default:

                                        /*if (!fields.containsKey(setSub)) {
                                            StreamlineCloud.log("Please enter a valid variable");
                                            return;
                                        }*/

                                        if (args.length != 6) {
                                            StreamlineCloud.log("Please enter a value");
                                            return;
                                        }

                                        Map<String, Object> values = Map.of(
                                                setSub, args[5]
                                        );

                                        try {
                                            Utils.setFieldsFromMap(group, values);
                                        } catch (IllegalAccessException | NoSuchFieldException e) {
                                            throw new RuntimeException(e);
                                        } catch (NumberFormatException e) {
                                            StreamlineCloud.log("Please enter a valid number");
                                        }
                                        break;

                                }

                            }
                            case "add" -> {

                                String addSub = args[4];

                                if (addSub.equals("template") || addSub.equals("t")) {

                                    group.getTemplates().add(args[5]);
                                    StreamlineCloud.log("sc.command.groups.templateAdded", new ReplacePaket[]{new ReplacePaket("%0", args[5]), new ReplacePaket("%1", group.getName())});

                                    StreamlineCloud.getGroupManager().update(group);

                                }
                            }
                            case "list" -> {

                                String listSub = args[4];

                                if (listSub.equals("templates")) {

                                    if (group.getTemplates().isEmpty()) {
                                        StreamlineCloud.log("sc.command.groups.list.templates.empty");
                                    } else {
                                        StreamlineCloud.log("sc.command.groups.list.templates.title",
                                                new ReplacePaket[]{new ReplacePaket("%0", group.getName())});
                                    }

                                    for (String template : group.getTemplates()) {
                                        StreamlineCloud.log("TEMPLATE: " + template);
                                    }

                                }
                            }

                            case "priority" -> {
                                String prioritySub = args[4];
                                int priority = 0;
                                try {
                                    priority = Integer.parseInt(prioritySub);
                                } catch (NumberFormatException e) {
                                    StreamlineCloud.getLogger().error("sc.command.groups.priority.nan");
                                }
                                group.setPriority(priority);
                                StreamlineCloud.log("sc.command.groups.priority.success");
                            }
                        }

                        StreamlineCloud.getGroupManager().update(group);

                    } else {

                        StreamlineCloud.log("sc.command.groups.notFound", new ReplacePaket[]{new ReplacePaket("%1", args[2])});

                    }

                } else {
                    StreamlineCloud.log("error");
                }

                break;
        }

        if (args[1].equals("help")) {
            StreamlineCloud.log("Basics");
            StreamlineCloud.log("- groups create <name> <server/proxy> <software> (optional: --static)");
            StreamlineCloud.log("- groups delete <name>");
            StreamlineCloud.log("Set data:");
            StreamlineCloud.log("- groups group <name> set minOnlineCount <int>");
            StreamlineCloud.log("- groups group <name> set minOnlineSoftware <string>");
            StreamlineCloud.log("- groups group <name> add template <string>");
            StreamlineCloud.log("- groups group <name> list templates");
            StreamlineCloud.log("Utils");
            StreamlineCloud.log("- listAvailableSoftware");
        }
    }

    public void sendHelp() {
        StreamlineCloud.log("Unknown Subcommand");
        StreamlineCloud.log("-> groups help");
    }
}
