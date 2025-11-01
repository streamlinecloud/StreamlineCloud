package net.streamlinecloud.main.command;

import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.group.CloudGroup;
import net.streamlinecloud.main.core.group.CloudGroupManager;
import net.streamlinecloud.main.lang.ReplacePaket;
import net.streamlinecloud.main.terminal.api.CloudCommand;
import net.streamlinecloud.main.utils.Cache;
import net.streamlinecloud.main.utils.Utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GroupsCommand extends CloudCommand {

    public GroupsCommand() {
        setName("groups");
        setAliases(new String[]{"g"});
        setDescription("Manage cloud groups");
    }

    @Override
    public void execute(String[] args) {

        if (args.length == 1) {
            sendHelp();
            return;
        }

        String sub = args[1];

        switch (sub) {
            case "create":

                if (args.length == 5 || args.length == 6) {

                    boolean staticGroup = (args.length == 6) && args[5].equals("--static");

                    String name = args[2];
                    String runtimeS = args[3];
                    ServerRuntime runtime;

                    if (runtimeS.equalsIgnoreCase("SERVER")) {
                        runtime = ServerRuntime.SERVER;
                    } else if (runtimeS.equalsIgnoreCase("PROXY")) {
                        runtime = ServerRuntime.PROXY;

                    } else {
                        StreamlineCloud.log("sc.command.groups.crate.enterValidRuntime");
                        return;
                    }

                    CloudGroup group = new CloudGroup(
                            name,
                            1,
                            new ArrayList<>(),
                            runtime,
                            args[4]);
                    group.setStaticGroup(staticGroup);

                    if (CloudGroupManager.getInstance().create(group))
                        StreamlineCloud.log("sc.command.groups.create.created", new ReplacePaket[]{new ReplacePaket("%1", group.getName())});
                    else StreamlineCloud.log("sc.command.groups.create.cantSave", new ReplacePaket[]{new ReplacePaket("%1", group.getName())});

                } else {
                    StreamlineCloud.log("syntax: - groups create <name> <server/proxy> <software> (optional: --static)");
                }

                break;
            case "delete":

                if (args.length == 3) {

                    CloudGroup group = CloudGroupManager.getInstance().getGroupByName(args[2]);

                    if (group != null) {

                        group.delete();
                        StreamlineCloud.log("sc.command.groups.delete.deleted", new ReplacePaket[]{new ReplacePaket("%1", group.getName())});

                    } else {

                        StreamlineCloud.log("sc.command.groups.notFound", new ReplacePaket[]{new ReplacePaket("%1", args[2])});

                    }

                } else {

                    StreamlineCloud.log("sc.command.groups.enterGroup");

                }

                break;
            case "list":

                StreamlineCloud.log("sc.command.groups.list.title");

                for (CloudGroup g : Cache.i().getActiveGroups()) {
                    StreamlineCloud.log(g.getName() + " - online: " + CloudGroupManager.getInstance().getGroupOnlineServers(g).size() + " - minOnline: " + g.getMinOnlineCount());
                }

                break;
            case "group":

                if (args.length == 2) {
                    StreamlineCloud.log("sc.command.groups.enterGroup");
                    return;
                }

                if (args[2] != null) {

                    CloudGroup group = CloudGroupManager.getInstance().getGroupByName(args[2]);

                    if (group != null) {

                        if (group.getName().equals(Cache.i().getDefaultGroup().getName())) {
                            StreamlineCloud.log("Nice try!");
                            return;
                        }

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

                        if (args.length == 3) {
                            StreamlineCloud.log("Information about " + group.getName());
                            fields.forEach((k, v) -> StreamlineCloud.log(k + ": " + v));
                            return;
                        }

                        String groupSub = args[3];

                        switch (groupSub) {
                            case "set" -> {

                                String setSub = args[4];

                                switch (setSub) {
                                    case "templates":
                                    case "name":
                                        break;

                                    default:

                                        if (!fields.containsKey(setSub)) {
                                            StreamlineCloud.log("Please enter a valid variable");
                                            return;
                                        }

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

                                    try {
                                        group.save();
                                    } catch (IOException e) {
                                        StreamlineCloud.log(e.getMessage());
                                    }

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
                                    StreamlineCloud.logError("sc.command.groups.priority.nan");
                                }
                                group.setPriority(priority);
                                StreamlineCloud.log("sc.command.groups.priority.success");
                            }
                        }

                        try {
                            group.save();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

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
