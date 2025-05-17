package net.streamlinecloud.main.command.completer;

import net.streamlinecloud.main.utils.Cache;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.List;

public class LanguageCommandCompleter implements Completer {

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        List<String> words = parsedLine.words();

        switch (words.size()) {
            case 2:
                list.add(new Candidate("set"));
                list.add(new Candidate("list"));
                break;

            case 3:
                Cache.i().getLanguages().forEach(lang -> {
                    list.add(new Candidate(lang.getName()));
                });
                break;
        }
    }
}
