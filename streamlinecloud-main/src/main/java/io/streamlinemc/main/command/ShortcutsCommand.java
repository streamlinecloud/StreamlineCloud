package io.streamlinemc.main.command;

import io.streamlinemc.main.CloudMain;
import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.terminal.api.CloudCommand;

public class ShortcutsCommand extends CloudCommand {

    public ShortcutsCommand() {
        setName("shortcuts");
        setAliases(new String[]{"kurzbefehle"});
        setDescription("StreamlineCloud shortcuts");
    }

    @Override
    public void execute(String[] args) {

        StreamlineCloud.log("All shortcuts");

        for (CloudCommand command : CloudMain.getInstance().getCommandMap()) {
            StringBuilder builder = new StringBuilder();
            if (command.aliases() != null) for (String a : command.aliases()) builder.append(a).append(",");
            StreamlineCloud.log("- " + command.name() + ": " + builder);
        }
    }
}
