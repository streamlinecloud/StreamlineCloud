package net.streamlinecloud.main.terminal.command;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class StreamlineCommandCompleter implements Completer {

    @NonNull
    StreamlineCommand command;

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        List<String> words = parsedLine.words();

        findCompletions(words.toArray(new String[0])).forEach(s -> {
            if (!s.startsWith("%")) list.add(new Candidate(s));
            else list.add(new Candidate("", s, null, null, null, null, true));
        });
    }

    /**
     * @param args Command args
     * @return All possible completions based on the command tree and the custom completers of the command. Undefiled variables start with '%'
     */
    List<String> findCompletions(String[] args) {
        List<String> commands = new ArrayList<>();

        if (!args[args.length - 1].startsWith("%")) {
            for (StreamlineSubcommand subcommand : findSubcommands(args)) {
                try {
                    String completion = subcommand.getName().split(" ")[args.length - 2];
                    boolean customFound = false;
                    if (completion.startsWith("%")) {
                        for (String completerPath : command.commandTree.completers.keySet()) {

                            if (subcommand.getName().startsWith(completerPath) && args.length - 1 == completerPath.split(" ").length) {
                                commands.addAll(command.commandTree.completers.get(completerPath).complete());
                                customFound = true;
                            }
                        }
                    }

                    if (!customFound) commands.add(completion);

                } catch (ArrayIndexOutOfBoundsException ignored) {
                }
            }
        }

        return commands;
    }

    /**
     * @param argsArray Command args
     * @return Returns all subcommands that could be meant
     * Takes a list of all subcommands and removes all that are not eligible for the given args
     */
    List<StreamlineSubcommand> findSubcommands(String[] argsArray) {
        List<StreamlineSubcommand> subcommands = new ArrayList<>(command.commandTree.tree);
        List<StreamlineSubcommand> toRemove = new ArrayList<>();

        List<String> args = new ArrayList<>(Arrays.stream(argsArray).toList());
        args.removeFirst();

        if (args.size() != 1) for (String arg : args) {
            for (StreamlineSubcommand subcommand : subcommands) {
                if (subcommand.getName().split(" ").length < args.size()) continue;
                String treeArg = subcommand.getName().split(" ")[args.indexOf(arg)];
                if (arg.isEmpty()) continue;
                if (!treeArg.equals(arg) && !treeArg.startsWith("%")) {
                    toRemove.add(subcommand);
                }
            }
        }

        subcommands.removeAll(toRemove);
        return subcommands;
    }
}
