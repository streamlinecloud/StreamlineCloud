package net.streamlinecloud.main.command;

import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.software.SoftwareManager;
import net.streamlinecloud.main.terminal.api.CloudCommand;
import net.streamlinecloud.main.utils.Cache;

import java.io.File;

public class SoftwareCommand extends CloudCommand {

    public SoftwareCommand() {
        setName("software");
        setAliases(new String[]{"serverSoftware"});
        setDescription("Manage server software");
    }

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
                StreamlineCloud.log("Software command usage");
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

                new File(Cache.i().getHomeFile() + "/data/software/" + name).mkdirs();

                if (uri[0].startsWith("http://") || uri[0].startsWith("https://")) {
                    StreamlineCloud.download(uri[0],Cache.i().getHomeFile() + "/data/software/" + name, success -> {
                        uri[0] = name;
                    });
                }

                SoftwareManager.getInstance().add(name, runtime, uri[0]);
                break;

            case "delete":
                StreamlineCloud.log("Software command usage");
                break;

            case "clearCache":
                StreamlineCloud.log("Software command usage");
                break;
        }

    }
}
