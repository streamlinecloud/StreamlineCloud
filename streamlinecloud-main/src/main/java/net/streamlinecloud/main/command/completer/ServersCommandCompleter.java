package net.streamlinecloud.main.command.completer;

import net.streamlinecloud.main.utils.Cache;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.List;

public class ServersCommandCompleter implements Completer {

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        List<String> words = parsedLine.words();

        switch (words.size()) {
            case 2:
                list.add(new Candidate("list", "list", null, null, null, null, true));
                list.add(new Candidate("start", "start", null, null, null, null, true));
                list.add(new Candidate("startnew", "startnew", null, null, null, null, true));
                list.add(new Candidate("server", "server", null, null, null, null, true));
                break;
            case 3:
                switch (words.get(1)) {
                    case "start":
                        list.add(new Candidate("", "<group>", null, null, null, null, true));
                    case "startnew":
                        list.add(new Candidate("", "<name>", null, null, null, null, true));
                        break;
                    case "server":

                        Cache.i().getRunningServers().forEach(server -> {
                            list.add(new Candidate(server.getName(), server.getName(), null, null, null, null, true));
                        });

                        break;
                    default:
                        break;
                }
                break;
            case 4:
                list.add(new Candidate("screen", "screen", null, null, null, null, true));
                list.add(new Candidate("kill", "kill", null, null, null, null, true));
                list.add(new Candidate("command", "command", null, null, null, null, true));
                break;
            default:
                break;
        }
    }

}
