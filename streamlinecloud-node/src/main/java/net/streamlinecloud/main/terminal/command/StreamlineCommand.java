package net.streamlinecloud.main.terminal.command;

import lombok.*;
import net.streamlinecloud.api.terminal.StreamlineLogger;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.command.completer.CommandCompleterManager;
import org.jline.reader.Completer;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@Setter
@RequiredArgsConstructor
public class StreamlineCommand {

    @NonNull
    String name, description;

    @NonNull
    StreamlineCommandTree commandTree;

    String[] aliases = new String[]{};

    StreamlineLogger logger = StreamlineCloud.getLogger();

    String defaultSubCommand = "help";

    public void run(String[] args) {

        if (args.length == 0) args = defaultSubCommand.split(" ");
        args = Arrays.stream(args).skip(1).toArray(String[]::new);

        if (args.length == 0) args = new String[]{defaultSubCommand};

        HashMap<String, Object> context = new HashMap<>();
        AtomicBoolean abort = new AtomicBoolean(false);

        context.put("abort", false);
        System.out.println("INP: " + args.toString());

        CommandAndChecks commandAndChecks = findCommandAndChecks(args);
        StreamlineSubcommand subcommand = commandAndChecks.subcommand;
        List<SubcommandCheckExecute> checks = commandAndChecks.checks;

        if (subcommand == null) {
            logger.info("This subcommand does not exist. Use the 'help' subcommand for more information.");
            return;
        }

        System.out.println("COMD: " + subcommand.name);

        for (SubcommandCheckExecute check : checks) {
            context.putAll(check.execute(getVariables(args), logger));
            if ((Boolean) context.get("abort")) abort.set(true);
        }

        if (abort.get()) return;

        HashMap<String, Object> finalContext = new HashMap<>(context);
        finalContext.putAll(getVariables(args));

        subcommand.execute.execute(finalContext, logger);

        /*

        for (String command : commandTree.checks.keySet()) {
            SubcommandCheckExecute execute = commandTree.checks.get(command);
            if (abort.get()) return;
            if (checkPath(command, commandFromUser.toString())) {
                context.putAll(execute.execute(getVariables(args), logger));
                if ((Boolean) context.get("abort")) abort.set(true);
            }
        }

        if (abort.get()) return;

        for (StreamlineSubcommand subCommand : commandTree.tree) {
            if (checkPathExact(subCommand.getName(), commandFromUser.toString())) {
                HashMap<String, Object> finalContext = new HashMap<>(context);
                finalContext.putAll(getVariables(args));

                subCommand.execute.execute(finalContext, logger);
            }
        }

        */

    }

    public boolean checkPath(String tree, String command) {
        if (command.startsWith(tree)) return true;

        int i = 0;

        for (String sub : tree.split(" ")) {
            if (!sub.startsWith("%")) if (!command.split(" ")[i].equals(sub)) return  false;
            i++;
        }

        return true;
    }

    public boolean checkPathExact(String tree, String command) {
        int i = 0;

        for (String sub : command.split(" ")) {
            System.out.println(tree + " - " + sub);
            if (tree.split(" ").length <= i) continue;
            if (!tree.split(" ")[i].startsWith("%")) if (!tree.split(" ")[i].equals(sub)) return  false;
            i++;
        }

        return true;
    }

    public HashMap<String, String> getVariables(String[] args) {
        HashMap<String, String> vars = new HashMap<>();

        for (StreamlineSubcommand command : commandTree.tree) {
            String[] tree = command.name.split(" ");

            for (String arg : args) {
                int index = Arrays.stream(args).toList().indexOf(arg);
                if ((index + 1) > tree.length) continue;
                if (tree[index].startsWith("%")) vars.put(tree[index].split("%")[1], arg);
            }

        }

        return vars;
    }

    /**
     * @param args Command args
     * @return Returns the SubCommand and all checks related to the given command args
     */
    public CommandAndChecks findCommandAndChecks(String[] args) {
        StreamlineSubcommand subcommand = null;

        for (StreamlineSubcommand subcommandInTree : commandTree.tree) {
            String[] treeArgs = subcommandInTree.name.split(" ");
            boolean match = true;

            if (treeArgs.length != args.length) continue;
            for (String treeArg : treeArgs) {
                if (!treeArg.equals(args[Arrays.asList(treeArgs).indexOf(treeArg)]) && !treeArg.startsWith("%")) match = false;
            }

            if (match) subcommand = subcommandInTree;
        }
        
        if (subcommand == null) return new CommandAndChecks(null, null);
        List<SubcommandCheckExecute> checks = new ArrayList<>();

        for (String name : commandTree.checks.keySet()) {
            if (subcommand.name.startsWith(name)) checks.add(commandTree.checks.get(name));
        }

        return new CommandAndChecks(subcommand, checks);
        
    }

    record CommandAndChecks(StreamlineSubcommand subcommand, List<SubcommandCheckExecute> checks) {}

    public static HashMap<String, Object> abort() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("abort", true);
        return map;
    }

    public static HashMap<String, Object> data(String key, Object val) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(key, val);
        return map;
    }

    public void addCompleter() {
        CommandCompleterManager.getInstance().getCompleters().put(getName(), new StreamlineCommandCompleter(this));
    }

    public void addCompleter(Completer completer) {
        CommandCompleterManager.getInstance().getCompleters().put(getName(), completer);
    }

}
