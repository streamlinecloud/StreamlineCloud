package net.streamlinecloud.main.terminal;

import net.streamlinecloud.api.exception.CommandException;
import net.streamlinecloud.api.extension.event.console.ExecuteCommandEvent;
import net.streamlinecloud.main.CloudMain;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.group.CloudGroupManager;
import net.streamlinecloud.main.core.server.CloudServerManager;
import net.streamlinecloud.main.terminal.api.CloudCommand;
import net.streamlinecloud.main.terminal.input.ConsoleQuestion;
import net.streamlinecloud.main.utils.Cache;
import lombok.SneakyThrows;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;

import java.util.Arrays;

import static net.streamlinecloud.main.extension.ExtensionManager.commandManager;
import static net.streamlinecloud.main.extension.ExtensionManager.eventManager;

public class CloudTerminalRunner extends Thread {

    private final CloudTerminal terminal;
    public CloudTerminalRunner(CloudTerminal terminal) {
        this.terminal = terminal;

        this.setDaemon(false);
        this.setName("StreamlineTerminalRunner");
        this.setPriority(1);
        this.start();
    }

    @SneakyThrows
    @Override
    public void run() {
        String line;
        while (true) {

            try {
                line = terminal.getLineReader().readLine(Color.translate("§REDuser §8-> "));


            if (line == null) break;

            if (Cache.i().getConsoleInputs().isEmpty()) {

                String[] args = line.split(" ");
                boolean executeCommands = true;

                if (Cache.i().getCurrentScreenServerName() != null) {

                    if (args[0].equals("cmd") || args[0].equals("c") || args[0].equals("command")) {

                        StringBuilder sb = new StringBuilder();

                        for (int i = 0; i <= args.length; i++) {

                            if (i < 2) continue;

                            sb.append(args[i - 1]).append(" ");
                        }

                        sb.deleteCharAt(sb.length() - 1);

                        CloudServerManager.getInstance().getServerByName(Cache.i().getCurrentScreenServerName()).addCommand(sb.toString());
                        executeCommands = false;

                    } else if (args[0].equals("exit")) {

                        CloudServerManager.getInstance().getServerByName(Cache.i().getCurrentScreenServerName()).disableScreen();
                        executeCommands = false;
                    }
                }


                if (executeCommands) {
                    ExecuteCommandEvent executeCommandEvent = eventManager.callEvent(new ExecuteCommandEvent(args[0], Arrays.stream(args).skip(1).toArray(String[]::new), null));
                    if (!executeCommandEvent.isCancelled()) {
                        executeCommand(args);
                    }
                }

            } else {

                ConsoleQuestion question = Cache.i().getConsoleInputs().get(0);
                question.execute(line);

                if (Cache.i().getConsoleInputs().isEmpty()) StreamlineCloud.releaseSavedLogs();
            }
            } catch (UserInterruptException ignore) {
                System.exit(130); //<--------- ONLY USE THIS AS A LAST RESORT
                //terminal.runHardStop();
            }
        }

    }

    @SneakyThrows
    public static void executeCommand(String[] args) {

        for (CloudCommand cloudCommand : CloudMain.getInstance().getCommandMap()) {

            if (cloudCommand.name().equals(args[0])) {

                cloudCommand.execute(args);
            }

            if (cloudCommand.aliases() != null) {

                for (String alias : cloudCommand.aliases()) {

                    if (alias.equals(args[0])) {

                        cloudCommand.execute(args);

                    }
                }
            }
        }


        commandManager.commands.values().forEach(command -> {
            if (command.getName().equals(args[0])) {
                try {
                    commandManager.executeCommand(command.getName(), Arrays.stream(args).skip(1).toArray(String[]::new));
                } catch (CommandException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }
}