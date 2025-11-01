package net.streamlinecloud.main.command;

import net.streamlinecloud.main.core.server.RunningServerManager;
import net.streamlinecloud.main.terminal.api.CloudCommand;
import net.streamlinecloud.main.utils.Cache;

public class SendToScreenCommand extends CloudCommand {

    public SendToScreenCommand() {
        setName("command");
        setAliases(new String[]{"cmd"});
        setDescription("Run a command in the current screen");
    }

    @Override
    public void execute(String[] args) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i <= args.length; i++) {

            if (i < 2) continue;

            sb.append(args[i - 1]).append(" ");
        }

        sb.deleteCharAt(sb.length() - 1);

        RunningServerManager.getInstance().getServerByName(Cache.i().getCurrentScreenServerName()).addCommand(sb.toString());
    }
}
