package io.streamlinemc.main.terminal;

import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.command.completer.MainCommandCompleter;
import lombok.Getter;
import org.jline.keymap.KeyMap;
import org.jline.reader.Binding;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.Reference;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class CloudTerminal {

    private final Terminal terminal;
    @Getter
    private final LineReader lineReader;
    @Getter
    private final CloudTerminalRunner runner;

    private boolean isInterupted = false;

    public CloudTerminal() {

        System.setProperty("jansi.passthrough", "true");

        try {

            this.terminal = TerminalBuilder.builder()
                    .system(true)
                    .dumb(true)
                    .encoding(StandardCharsets.UTF_8)
                    .build();

            /*
            Terminal.SignalHandler signalHandler = signal -> runHardStop();

            terminal.handle(Terminal.Signal.INT, signalHandler);
             */

            this.lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .option(LineReader.Option.AUTO_FRESH_LINE, true)
                    .option(LineReader.Option.AUTO_MENU_LIST, true)
                    .option(LineReader.Option.EMPTY_WORD_OPTIONS, true)
                    .option(LineReader.Option.HISTORY_TIMESTAMPED, true)
                    .option(LineReader.Option.DISABLE_EVENT_EXPANSION, true)
                    .option(LineReader.Option.AUTO_GROUP, true)
                    .option(LineReader.Option.AUTO_LIST, true)
                    .completer(new MainCommandCompleter())
                    .build();

            lineReader.variable(LineReader.BELL_STYLE, "none");
            lineReader.variable(LineReader.HISTORY_SIZE, 1000);
            lineReader.variable(LineReader.HISTORY_FILE_SIZE, 2000);
            lineReader.variable(LineReader.HISTORY_FILE, System.getProperty("user.dir") + "/.cloud_history");


            this.runner = new CloudTerminalRunner(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {

        try {
            this.runner.interrupt();
            this.terminal.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(String input) {
        this.terminal.puts(InfoCmp.Capability.carriage_return);
        this.terminal.writer().println(Color.translate(input));
        this.terminal.flush();
    }

    public void runHardStop() {
        if (!isInterupted) {

            StreamlineCloud.log("§DARK_REDCloud Interruption detected. To Hard Stop the Cloud press CTRL+C again in the next 10 Seconds!");

            ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

            scheduledExecutorService.schedule(() -> {
                isInterupted = false;
            }, 10, java.util.concurrent.TimeUnit.SECONDS);

            isInterupted = true;
            return;
        }

        StreamlineCloud.log("§DARK_REDCloud Hard Stopping...");
        System.exit(130);
    }

}
