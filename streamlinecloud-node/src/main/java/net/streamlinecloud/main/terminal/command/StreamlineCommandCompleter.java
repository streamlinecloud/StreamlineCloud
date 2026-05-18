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

        findCompletions(words.toArray(new String[0])).forEach(s -> list.add(new Candidate(s)));
    }

    /**
     * @param args Command args
     * @return All possible completions based on the command tree and the custom completers of the command. Undefiled variables start with '%'
     */
    List<String> findCompletions(String[] args) {
        List<String> commands = new ArrayList<>();

        command.logger.info("Completing " + Arrays.toString(args));

        if (!args[args.length - 1].startsWith("%")) {
            findSubcommands(args).forEach(subcommand -> {
                try {
                    commands.add(subcommand.getName().split(" ")[args.length - 2]);
                } catch (ArrayIndexOutOfBoundsException ignored) {}
            });
        }

        for (StreamlineSubcommand command : command.commandTree.tree) {

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
                if (!treeArg.equals(arg) && !treeArg.startsWith("%")) {
                    toRemove.add(subcommand);
                }
            }
        }

        subcommands.removeAll(toRemove);
        return subcommands;
    }
}
