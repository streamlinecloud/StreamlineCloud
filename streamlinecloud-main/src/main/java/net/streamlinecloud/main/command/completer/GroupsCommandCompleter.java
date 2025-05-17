package net.streamlinecloud.main.command.completer;

import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.utils.Cache;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.List;

public class GroupsCommandCompleter implements Completer {

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        List<String> words = parsedLine.words();

        switch (words.size()) {
            case 2:
                list.add(new Candidate("group"));
                list.add(new Candidate("create"));
                break;

            case 3:
                if (words.get(1).equals("create")) {
                    list.add(new Candidate("", "<name>", null, null, null, null, true));
                } else if (words.get(1).equals("group")) {
                    Cache.i().getActiveGroups().forEach(group -> {
                        if (!group.getName().equals("WITHOUT")) list.add(new Candidate(group.getName()));
                    });
                }
                break;

            case 4:
                if (words.get(1).equals("create")) {
                    list.add(new Candidate("server"));
                    list.add(new Candidate("proxy"));
                } else if (words.get(1).equals("group")) {
                    list.add(new Candidate("set"));
                    list.add(new Candidate("add"));
                    list.add(new Candidate("list"));
                }
                break;

            case 5:
                switch (words.get(3)) {
                    case "set" -> list.add(new Candidate("minOnlineCount"));
                    case "add" -> list.add(new Candidate("template"));
                    case "list" -> list.add(new Candidate("templates"));
                }
                break;
        }
    }
}
