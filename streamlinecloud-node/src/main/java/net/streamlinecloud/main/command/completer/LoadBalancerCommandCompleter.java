package net.streamlinecloud.main.command.completer;

import net.streamlinecloud.main.utils.Cache;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.List;

public class LoadBalancerCommandCompleter implements Completer {

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        List<String> words = parsedLine.words();

        switch (words.size()) {
            case 2:
                list.add(new Candidate("list"));
                list.add(new Candidate("create"));
                list.add(new Candidate("delete"));
                break;

            case 3:
                if (!(words.get(1).equals("delete") || words.get(1).equals("create"))) return;
                list.add(new Candidate("", "<name>", null, null, null, null, true));
                break;

            case 4:
                if (!words.get(1).equals("create")) return;
                list.add(new Candidate("", "<port>", null, null, null, null, true));
                break;

            case 5:
                if (!words.get(1).equals("create")) return;
                list.add(new Candidate("", "<targetGroup>", null, null, null, null, true));
                break;
        }
    }
}
