package net.streamlinecloud.main.command;

import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.group.CloudGroup;
import net.streamlinecloud.main.lang.ReplacePaket;
import net.streamlinecloud.main.terminal.api.CloudCommand;
import net.streamlinecloud.main.utils.Cache;

import java.io.IOException;
import java.util.ArrayList;

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

                if (args.length == 4 || args.length == 5) {

                    boolean staticGroup = (args.length == 5) && args[4].equals("--static");

                    String name = args[2];
                    String runtimeS = args[3];
                    ServerRuntime runtime = null;

                    if (runtimeS.equalsIgnoreCase("SERVER")) {
                        runtime = ServerRuntime.SERVER;
                    } else if (runtimeS.equalsIgnoreCase("PROXY")) {
                        runtime = ServerRuntime.PROXY;

                    } else {
                        StreamlineCloud.log("sl.command.groups.crate.enterValidRuntime");
                        return;
                    }

                    CloudGroup group = new CloudGroup(
                            name,
                            1,
                            new ArrayList<>(), runtime);
                    group.setStaticGroup(staticGroup);

                    try {
                        group.save();
                    } catch (IOException e) {
                        StreamlineCloud.log("sl.command.groups.create.cantSave", new ReplacePaket[]{new ReplacePaket("%1", e.getMessage())});
                        return;
                    }

                    Cache.i().getActiveGroups().add(group);
                    StreamlineCloud.log("sl.command.groups.create.created", new ReplacePaket[]{new ReplacePaket("%1", group.getName())});

                } else {
                    StreamlineCloud.log("syntax: groups create <name> <template> <server/proxy> optional: --static");
                }

                break;
            case "delete":

                if (args.length == 3) {

                    CloudGroup group = StreamlineCloud.getGroupByName(args[2]);

                    if (group != null) {

                        group.delete();
                        StreamlineCloud.log("sl.command.groups.delete.deleted", new ReplacePaket[]{new ReplacePaket("%1", group.getName())});

                    } else {

                        StreamlineCloud.log("sl.command.groups.notFound", new ReplacePaket[]{new ReplacePaket("%1", args[2])});

                    }

                } else {

                    StreamlineCloud.log("sl.command.groups.enterGroup");

                }

                break;
            case "list":

                StreamlineCloud.log("sl.command.groups.list.title");

                for (CloudGroup g : Cache.i().getActiveGroups()) {
                    StreamlineCloud.log(g.getName() + " - online: " + StreamlineCloud.getGroupOnlineServers(g).size() + " - minOnline: " + g.getMinOnlineCount());
                }

                break;
            case "group":

                if (args.length == 2) {
                    StreamlineCloud.log("sl.command.groups.enterGroup");
                    return;
                }

                if (args[2] != null) {

                    CloudGroup group = StreamlineCloud.getGroupByName(args[2]);

                    if (group != null) {

                        if (group.getName().equals(Cache.i().getDefaultGroup().getName())) {
                            StreamlineCloud.log("Nice try!");
                            return;
                        }

                        String groupSub = args[3];

                        switch (groupSub) {
                            case "set" -> {

                                String setSub = args[4];

                                if (setSub.equalsIgnoreCase("minOnlineCount") || setSub.equals("moc")) {

                                    if (args.length == 6) {

                                        try {
                                            group.setMinOnlineCount(Integer.parseInt(args[5]));
                                        } catch (NumberFormatException e) {
                                            StreamlineCloud.log("Please enter a valid number");
                                            return;
                                        }

                                    } else {

                                        StreamlineCloud.log("Enter a number");
                                        return;

                                    }

                                }
                            }
                            case "add" -> {

                                String addSub = args[4];

                                if (addSub.equals("template") || addSub.equals("t")) {

                                    group.getTemplates().add(args[5]);
                                    StreamlineCloud.log("sl.command.groups.templateAdded", new ReplacePaket[]{new ReplacePaket("%0", args[5]), new ReplacePaket("%1", group.getName())});

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
                                        StreamlineCloud.log("sl.command.groups.list.templates.empty");
                                    } else {
                                        StreamlineCloud.log("sl.command.groups.list.templates.title",
                                                new ReplacePaket[]{new ReplacePaket("%0", group.getName())});
                                    }

                                    for (String template : group.getTemplates()) {
                                        StreamlineCloud.log("TEMPLATE: " + template);
                                    }

                                }
                            }
                        }

                        try {
                            group.save();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    } else {

                        StreamlineCloud.log("sl.command.groups.notFound", new ReplacePaket[]{new ReplacePaket("%1", args[2])});

                    }

                } else {
                    StreamlineCloud.log("error");
                }

                break;
        }

        if (args[1].equals("help")) {
            StreamlineCloud.log("Basics");
            StreamlineCloud.log("- groups create <name> <server/proxy>");
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
