package net.streamlinecloud.main.command;

import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.terminal.api.CloudCommand;

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
                StreamlineCloud.log("Software command usage");
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
