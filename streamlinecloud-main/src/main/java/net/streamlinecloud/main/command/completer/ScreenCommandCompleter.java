package net.streamlinecloud.main.command.completer;

import net.streamlinecloud.main.core.server.CloudServerManager;
import net.streamlinecloud.main.utils.Cache;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.List;

public class ScreenCommandCompleter implements Completer {

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        List<String> words = parsedLine.words();

        if (words.size() == 2) {
            CloudServerManager.getInstance().getRunningServers().forEach(server -> {
                list.add(new Candidate(server.getName()));
            });
        }
    }
}
