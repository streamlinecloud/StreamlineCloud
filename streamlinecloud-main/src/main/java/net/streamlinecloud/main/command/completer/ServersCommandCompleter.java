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

        if (words.isEmpty()) return;

        String command = words.get(0);

        if (!command.equals("servers")) return;

        switch (words.size()) {
            case 2:
                addFirstArgumentCandidates(list);
                break;
            case 3:
                addSecondArgumentCandidates(words.get(1), list);
                break;
            case 4:
                addThirthArgumentCandidates(list);
                break;
            default:
                break;
        }
    }

    private void addFirstArgumentCandidates(List<Candidate> list) {
        list.add(new Candidate("list", "list", null, null, null, null, true));
        list.add(new Candidate("start", "start", null, null, null, null, true));
        list.add(new Candidate("startnew", "startnew", null, null, null, null, true));
        list.add(new Candidate("server", "server", null, null, null, null, true));
    }

    private void addSecondArgumentCandidates(String subCommand, List<Candidate> list) {
        switch (subCommand) {
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
    }

    private void addThirthArgumentCandidates(List<Candidate> list) {
                list.add(new Candidate("screen", "screen", null, null, null, null, true));
                list.add(new Candidate("kill", "kill", null, null, null, null, true));
                list.add(new Candidate("command", "command", null, null, null, null, true));
    }
}
