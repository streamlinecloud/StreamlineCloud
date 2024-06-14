package io.streamlinemc.main.command;

import io.streamlinemc.api.server.ServerRuntime;
import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.core.group.CloudGroup;
import io.streamlinemc.main.lang.ReplacePaket;
import io.streamlinemc.main.terminal.api.CloudCommand;
import io.streamlinemc.main.utils.StaticCache;

import java.io.IOException;
import java.util.Arrays;

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
                    String template = args[3];
                    String runtimeS = args[4];
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
                            StaticCache.getConfig().getDefaultJavaPath(),
                            1,
                            Arrays.asList(new String[]{template}), runtime);
                    group.setStaticGroup(staticGroup);

                    try {
                        group.save();
                    } catch (IOException e) {
                        StreamlineCloud.log("sl.command.groups.create.cantSave", new ReplacePaket[]{new ReplacePaket("%1", e.getMessage())});
                        return;
                    }

                    StaticCache.getActiveGroups().add(group);
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

                for (CloudGroup g : StaticCache.getActiveGroups()) {
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

                        if (group.getName().equals(StaticCache.getDefaultGroup().getName())) {
                            StreamlineCloud.log("Nice try!");
                            return;
                        }

                        String groupSub = args[3];

                        if (groupSub.equals("set")) {

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

                        } else if (groupSub.equals("add")) {

                            String addSub = args[4];

                            if (addSub.equals("template") || addSub.equals("t")) {

                                group.getTemplates().add(args[5]);
                                StreamlineCloud.log("sl.command.groups.templateAdded", new ReplacePaket[]{new ReplacePaket("%0", args[5]), new ReplacePaket("%1", group.getName())});

                                try {
                                    group.save();
                                } catch (IOException e) {
                                    StreamlineCloud.log(e.getMessage());
                                    e.printStackTrace();
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
            StreamlineCloud.log("- groups create <name> <template> <server/proxy>");
            StreamlineCloud.log("Set data:");
            StreamlineCloud.log("- groups group set minOnlineCount <int>");
            StreamlineCloud.log("- groups group set minOnlineSoftware <string>");
            StreamlineCloud.log("- groups group add template <string>");
            StreamlineCloud.log("Utils");
            StreamlineCloud.log("- listAvailableSoftware");
            StreamlineCloud.log("- downloadSoftware <proxy/server> <link>");
        }
    }

    public void sendHelp() {
        StreamlineCloud.log("Unknown Subcommand");
        StreamlineCloud.log("-> groups help");
    }
}
