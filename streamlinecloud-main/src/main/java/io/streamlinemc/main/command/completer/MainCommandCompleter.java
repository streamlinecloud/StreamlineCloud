package io.streamlinemc.main.command.completer;

import io.streamlinemc.main.CloudMain;
import io.streamlinemc.main.utils.StaticCache;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainCommandCompleter implements Completer {

    private final Map<String, Completer> completers = new HashMap<>();

    public MainCommandCompleter() {
        completers.put("servers", new ServersCommandCompleter());
    }

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        List<String> words = parsedLine.words();

        if (words.isEmpty()) return;

        String command = words.get(0);

        Completer completer = completers.get(command);

        if (completer != null) {
            completer.complete(lineReader, parsedLine, list);
        } else {
            CloudMain.getInstance().getCommandMap().forEach(cmd -> {
                list.add(new Candidate(cmd.name(), cmd.name(), "command", cmd.description(), null, null, true));

                if (cmd.aliases() != null) {
                    for (String alias : cmd.aliases()) {
                        list.add(new Candidate(cmd.name(), alias, "command", "[" + cmd.name() + "] " + cmd.description(), null, null, true));
                    }
                }
            });
        }

    }
}
