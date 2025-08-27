package net.streamlinecloud.main.command.completer;

import net.streamlinecloud.api.software.StreamlineSoftware;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.software.SoftwareConfig;
import net.streamlinecloud.main.core.software.SoftwareManager;
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
                list.add(new Candidate("delete"));
                break;

            case 3:
                if (words.get(1).equals("create")) {
                    list.add(new Candidate("", "<name>", null, null, null, null, true));
                } else if (words.get(1).equals("group") || words.get(1).equals("delete")) {
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
                if (words.get(1).equals("create")) {
                    SoftwareConfig softwareConfig = (SoftwareConfig) SoftwareManager.getInstance().getConfig().getData();
                    for (StreamlineSoftware software : softwareConfig.software) {
                        list.add(new Candidate(software.getName()));
                    }
                }
                switch (words.get(3)) {
                    case "set":
                        list.add(new Candidate("minOnlineCount"));
                        list.add(new Candidate("software"));
                        list.add(new Candidate("autoRestartMinutes"));
                        break;

                    case "add":
                        list.add(new Candidate("template"));
                        break;

                    case "list":
                        list.add(new Candidate("templates"));
                        break;
                }
                break;
        }
    }
}
