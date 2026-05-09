package net.streamlinecloud.main.terminal.command;

import com.google.gson.Gson;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.List;

@RequiredArgsConstructor
public class StreamlineCommandCompleter implements Completer {

    @NonNull
    StreamlineCommand command;

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        List<String> words = parsedLine.words();

        for (StreamlineSubcommand subCommand : command.commandTree.tree) {
            String current = words.getLast();
            if (words.indexOf(current) - 1 >= subCommand.getName().split(" ").length) continue;
            String completion = subCommand.getName().split(" ")[words.indexOf(current) - 1];

            if (completion.startsWith("%")) list.add(new Candidate("", "<" + completion.split("%")[1] + ">", null, null, null, null, true));
            else list.add(new Candidate(completion));
        }
    }
}
