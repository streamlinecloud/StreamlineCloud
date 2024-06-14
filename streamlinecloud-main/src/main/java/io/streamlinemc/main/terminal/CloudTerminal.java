package io.streamlinemc.main.terminal;

import io.streamlinemc.main.StreamlineCloud;
import lombok.Getter;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
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
                    .option(LineReader.Option.DISABLE_EVENT_EXPANSION, true)
                    .build();

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
