package net.streamlinecloud.main.command;

import lombok.SneakyThrows;
import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.api.software.StreamlineSoftware;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.group.CloudGroup;
import net.streamlinecloud.main.core.group.CloudGroupManager;
import net.streamlinecloud.main.core.software.SoftwareCatalogItem;
import net.streamlinecloud.main.core.software.SoftwareConfig;
import net.streamlinecloud.main.core.software.SoftwareManager;
import net.streamlinecloud.main.terminal.api.CloudCommand;
import net.streamlinecloud.main.utils.Cache;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SoftwareCommand extends CloudCommand {

    public SoftwareCommand() {
        setName("software");
        setAliases(new String[]{"serverSoftware"});
        setDescription("Manage server software");
    }

    @SneakyThrows
    @Override
    public void execute(String[] args) {

        if (args.length == 1) {
            StreamlineCloud.log("Type 'software help' for more information");
            return;
        }

        switch (args[1]) {
            case "help":
                StreamlineCloud.log("Software command usage");
                StreamlineCloud.log("- software list");
                StreamlineCloud.log("- software add <name> <server/proxy> <url/path>");
                StreamlineCloud.log("- software delete <name>");
                StreamlineCloud.log("- software clearCache <name>");
                break;

            case "list":
                StreamlineCloud.log("Installed software:");
                SoftwareConfig config = (SoftwareConfig) SoftwareManager.getInstance().config.getData();
                for (StreamlineSoftware software : config.software) {
                    StreamlineCloud.log("- " + software.getName());
                }
                break;

            case "add":
                String name = args[2];
                String runtimeStr = args[3];
                ServerRuntime runtime;
                final String[] uri = {args[4]};

                if (runtimeStr.equalsIgnoreCase("server")) runtime = ServerRuntime.SERVER;
                else if (runtimeStr.equalsIgnoreCase("proxy")) runtime = ServerRuntime.PROXY;
                else {
                    StreamlineCloud.log("Please enter a valid runtime");
                    return;
                }

                SoftwareManager.getInstance().add(name, runtime, uri[0]);
                break;

            case "delete":
                String softwareName = args[2];
                if (softwareName == null || softwareName.isEmpty()) {
                    StreamlineCloud.log("Please define a target software");
                    return;
                }

                boolean warn = false;
                List<String> groups = new ArrayList<>();

                for (CloudGroup activeGroup : CloudGroupManager.getInstance().getActiveGroups()) {
                    if (activeGroup.getName().equals(softwareName)) {
                        warn = true;
                        groups.add(activeGroup.getName());
                    }
                }

                if (warn) {
                    StreamlineCloud.log("WARNING! The following groups are currently using this software:");
                    for (String group : groups) StreamlineCloud.log("- " + group);
                    StreamlineCloud.log("Please make sure this software is not used by any group before you delete it");
                    return;
                }

                SoftwareManager.getInstance().delete(softwareName);
                StreamlineCloud.log("Software " + softwareName + " deleted");
                break;

            case "clearCache":
                softwareName = args[2];
                if (softwareName == null || softwareName.isEmpty()) {
                    StreamlineCloud.log("Please define a target software");
                    return;
                }

                StreamlineSoftware software = SoftwareManager.getInstance().getSoftware(softwareName);
                if (software == null) {
                    StreamlineCloud.log("The software " + softwareName + " does not exist");
                    return;
                }

                software.setCached(false);
                FileUtils.delete(new File(Cache.i().getHomeFile() + "/data/software/" + software.getFolder() + "/cache"));
                SoftwareManager.getInstance().replace(softwareName, software);

                StreamlineCloud.log("The cache for " + softwareName + " has been cleared");
                break;

            case "catalog":
                String catalogArg = args[2];

                if (catalogArg.equals("list")) {
                    StreamlineCloud.log("This is the full software catalog:");
                    for (SoftwareCatalogItem item : SoftwareManager.getInstance().getCatalog()) {
                        StreamlineCloud.log(item.getSoftware() + " - " + item.getVersion() + " (" + (SoftwareManager.getInstance().getSoftware(item.getSoftware() + "-" + item.getVersion()) == null ? "not installed" : "installed") + ")");
                    }

                } else if (catalogArg.equals("install")) {

                    String serverSoftware = args[3];
                    String version = args[4];

                    if (serverSoftware.isEmpty() || version.isEmpty()) {
                        StreamlineCloud.log("Please enter a software name and version");
                        return;
                    }

                    for (SoftwareCatalogItem item : SoftwareManager.getInstance().getCatalog()) {
                        if (item.getSoftware().equals(serverSoftware) && item.getVersion().equals(version)) {
                            runtime = ServerRuntime.SERVER;
                            if (item.getSoftware().equals("velocity")) runtime = ServerRuntime.PROXY;
                            SoftwareManager.getInstance().add(item.getSoftware() + "-" + item.getVersion(), runtime, item.getUrl());
                            StreamlineCloud.log("Installed successful");
                            return;
                        }
                    }

                    StreamlineCloud.log("This software is not available");
                }
                break;
        }

    }
}
