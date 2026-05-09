package net.streamlinecloud.main.command.completer;

import lombok.Getter;
import net.streamlinecloud.main.CloudMain;
import net.streamlinecloud.main.StreamlineCloud;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class CommandCompleterManager implements Completer {

    @Getter
    public static CommandCompleterManager instance;

    final Map<String, Completer> completers = new HashMap<>();

    public CommandCompleterManager() {
        instance = this;

        completers.put("servers", new ServersCommandCompleter());
        completers.put("s", new ServersCommandCompleter());

        completers.put("whitelist", new WhitelistCommandCompleter());
        completers.put("wl", new WhitelistCommandCompleter());

        completers.put("groups", new GroupsCommandCompleter());
        completers.put("g", new GroupsCommandCompleter());

        completers.put("language", new LanguageCommandCompleter());
        completers.put("lang", new LanguageCommandCompleter());

        completers.put("software", new SoftwareCommandCompleter());

        completers.put("screen", new ScreenCommandCompleter());

        completers.put("loadbalancer", new LoadBalancerCommandCompleter());
        completers.put("lb", new LoadBalancerCommandCompleter());
    }

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        List<String> words = parsedLine.words();

        if (words.isEmpty()) return;

        String command = words.get(0);

        Completer completer = completers.get(command);

        if (completer != null) {
            completer.complete(lineReader, parsedLine, list);
        } else {
            StreamlineCloud.getCommandManager().getCommandMap().forEach(cmd -> {
                list.add(new Candidate(cmd.getName(), cmd.getName(), "command", cmd.getDescription(), null, null, true));

                if (cmd.getAliases() != null) {
                    for (String alias : cmd.getAliases()) {
                        list.add(new Candidate(cmd.getName(), alias, "command", "[" + cmd.getName() + "] " + cmd.getDescription(), null, null, true));
                    }
                }
            });
        }

    }
}
