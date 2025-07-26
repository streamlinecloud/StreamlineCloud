package net.streamlinecloud.main.command.completer;

import net.streamlinecloud.main.utils.Cache;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.List;

public class SoftwareCommandCompleter implements Completer {

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        List<String> words = parsedLine.words();

        switch (words.size()) {
            case 2:
                list.add(new Candidate("help"));
                list.add(new Candidate("list"));
                list.add(new Candidate("add"));
                list.add(new Candidate("delete"));
                list.add(new Candidate("clearCache"));
                break;
            case 3:
                switch (words.get(1)) {
                    case "add": case "delete": case "clearCache":
                        list.add(new Candidate("", "<name>", null, null, null, null, true));

                    case "server":

                        Cache.i().getRunningServers().forEach(server -> {
                            list.add(new Candidate(server.getName()));
                        });

                        break;
                    default:
                        break;
                }
                break;
            case 4:
                if (words.get(1).equals("add")) {
                    list.add(new Candidate("server"));
                    list.add(new Candidate("proxy"));
                }
                break;
            case 5:
                if (words.get(1).equals("add")) {
                    list.add(new Candidate("", "<url/path>", null, "Enter a download url or a location in your file system", null, null, true));
                }
                break;
            default:
                break;
        }

    }
}
