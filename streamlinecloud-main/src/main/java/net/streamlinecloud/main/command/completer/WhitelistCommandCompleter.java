package net.streamlinecloud.main.command.completer;

import net.streamlinecloud.main.utils.Cache;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.List;

public class WhitelistCommandCompleter implements Completer {

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        List<String> words = parsedLine.words();

        switch (words.size()) {

            case 2:
                list.add(new Candidate("add", "add", null, null, null, null, true));
                list.add(new Candidate("remove", "remove", null, null, null, null, true));
                list.add(new Candidate("list", "list", null, null, null, null, true));
                list.add(new Candidate("enable", "enable", null, null, null, null, true));
                list.add(new Candidate("disable", "disable", null, null, null, null, true));
                break;

            case 3:
                if (words.get(1).equals("add"))
                    list.add(new Candidate("", "<mcName>", null, null, null, null, true));
                else Cache.i().getConfig().getWhitelist().getWhitelist().forEach(whitelist -> {
                    list.add(new Candidate(whitelist, whitelist, null, null, null, null, true));
                });
                break;

        }
    }
}
