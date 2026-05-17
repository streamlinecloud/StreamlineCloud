package net.streamlinecloud.main.terminal;

import lombok.Getter;
import lombok.Setter;
import net.streamlinecloud.api.extension.event.console.ConsoleInputEvent;
import net.streamlinecloud.api.extension.event.console.ExecuteCommandEvent;
import net.streamlinecloud.main.CloudMain;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.extension.ExtensionManager;
import net.streamlinecloud.main.terminal.api.CloudCommand;
import net.streamlinecloud.main.terminal.command.StreamlineCommand;
import net.streamlinecloud.main.utils.Cache;
import lombok.SneakyThrows;
import org.jline.reader.EndOfFileException;
import org.jline.reader.UserInterruptException;

import java.util.Arrays;

import static net.streamlinecloud.main.extension.ExtensionManager.commandManager;
import static net.streamlinecloud.main.extension.ExtensionManager.eventManager;

@Getter
public class CloudTerminalRunner extends Thread {

    private boolean restricted = false;
    private final CloudTerminal terminal;
    private long lastTimeCtrlCPressed = 0;

    @Setter
    public String screenIndicator;

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
                line = terminal.getLineReader().readLine(Color.translate("§RED" + (screenIndicator == null || screenIndicator.isEmpty() ? "StreamlineCloud" : screenIndicator) + " §8-> "));
                if (line == null) break;

                String[] args = line.split(" ");

                ExtensionManager.eventManager.callEvent(new ConsoleInputEvent(line));

                if (restricted) continue;

                ExecuteCommandEvent executeCommandEvent = eventManager.callEvent(new ExecuteCommandEvent(args[0], Arrays.stream(args).skip(1).toArray(String[]::new), null));
                if (!executeCommandEvent.isCancelled()) {
                    executeCommand(args);
                }


            } catch (UserInterruptException ignore) {
                if (System.currentTimeMillis() - lastTimeCtrlCPressed > 1000 * 3) {
                    StreamlineCloud.log("sc.ctrlC");
                    lastTimeCtrlCPressed = System.currentTimeMillis();
                    return;

                } else {
                    StreamlineCloud.shutDown();

                }

            } catch (EndOfFileException e) {
                StreamlineCloud.getLogger().error("(Terminal) End of file reached.");

            }

        }

    }

    @SneakyThrows
    public static void executeCommand(String[] args) {
        for (StreamlineCommand command : StreamlineCloud.getCommandManager().getCommandMap()) {
            if (command.getName().equals(args[0])) {
                executeCommand(command, args);

            }

            if (command.getAliases() != null) {
                for (String alias : command.getAliases()) {
                    if (alias.equals(args[0])) {
                        executeCommand(command, args);

                    }
                }
            }
        }


        commandManager.commands.values().forEach(command -> {
            if (command.getName().equals(args[0])) {
                commandManager.executeCommand(command.getName(), Arrays.stream(args).skip(1).toArray(String[]::new));
            }
        });

    }

    public static void executeCommand(StreamlineCommand command, String[] args) {
        try {
            command.run(args);

        } catch (Exception e) {
            StreamlineCloud.log("An error occurred while executing this command. Enable debugs for more details.");
            if (Cache.i().isDebugMode()) e.printStackTrace();
            e.printStackTrace();

        }
    }

    public void setRestricted(boolean restricted) {
        this.restricted = restricted;

        if (!restricted) {
            getTerminal().getSavedLogs().forEach(NodeLogger::logSingle);
            getTerminal().getSavedLogs().removeAll(getTerminal().getSavedLogs());
        }
    }

}