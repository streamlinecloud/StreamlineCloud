package io.streamlinemc.main.terminal;

import io.streamlinemc.api.plmanager.event.predefined.ExecuteCommandEvent;
import io.streamlinemc.main.CloudMain;
import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.terminal.api.CloudCommand;
import io.streamlinemc.main.terminal.input.ConsoleInput;
import io.streamlinemc.main.utils.StaticCache;
import lombok.SneakyThrows;
import org.jline.reader.UserInterruptException;

import java.util.Arrays;

import static io.streamlinemc.main.plugin.PluginManager.eventManager;

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

            if (StaticCache.getConsoleInputs().isEmpty()) {

                String[] args = line.split(" ");
                boolean executeCommands = true;

                if (StaticCache.getCurrentScreenServerName() != null) {

                    if (args[0].equals("cmd") || args[0].equals("c") || args[0].equals("command")) {

                        StringBuilder sb = new StringBuilder();

                        for (int i = 0; i <= args.length; i++) {

                            if (i < 2) continue;

                            sb.append(args[i - 1]).append(" ");
                        }

                        sb.deleteCharAt(sb.length() - 1);

                        StreamlineCloud.getServerByName(StaticCache.getCurrentScreenServerName()).addCommand(sb.toString());
                        executeCommands = false;

                    } else if (args[0].equals("exit")) {

                        StreamlineCloud.getServerByName(StaticCache.getCurrentScreenServerName()).disableScreen();
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

                ConsoleInput input = StaticCache.getConsoleInputs().get(0);

                if (input.getInputType().equals(ConsoleInput.InputType.INT)) {

                    try {

                        int i = Integer.parseInt(line);

                    } catch (NumberFormatException ex) {

                        StreamlineCloud.log("Bitte gebe eine gültige Zahl ein!");
                        return;
                    }
                } else if (input.getInputType().equals(ConsoleInput.InputType.BOOLEAN)) {

                    if (!line.equals("true") && !line.equals("false")) {

                        StreamlineCloud.log("Bitte gebe true oder false ein");
                        continue;
                    }
                }


                input.getNext().execute(line);
                StaticCache.getConsoleInputs().remove(input);

                if (StaticCache.getConsoleInputs().isEmpty()) StreamlineCloud.releaseSavedLogs();
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
    }
}