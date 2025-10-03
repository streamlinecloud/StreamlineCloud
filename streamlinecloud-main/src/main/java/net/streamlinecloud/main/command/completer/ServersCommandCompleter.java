package net.streamlinecloud.main.command.completer;

import net.streamlinecloud.main.core.server.RunningServerManager;
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
                list.add(new Candidate("list"));
                list.add(new Candidate("start"));

                RunningServerManager.getInstance().getRunningServers().forEach(server -> {
                    list.add(new Candidate(server.getName()));
                });
                break;
            case 3:
                switch (words.get(1)) {
                    case "start":
                        list.add(new Candidate("", "<group/name>", null, null, null, null, true));
                        break;
                    default:
                        list.add(new Candidate("stop"));
                        list.add(new Candidate("kill"));
                        list.add(new Candidate("restart"));
                        list.add(new Candidate("command"));
                        break;
                }
                break;
            default:
                break;
        }
    }

}
