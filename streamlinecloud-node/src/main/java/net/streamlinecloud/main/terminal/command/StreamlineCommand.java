package net.streamlinecloud.main.terminal.command;

import lombok.*;
import net.streamlinecloud.api.terminal.StreamlineLogger;
import net.streamlinecloud.main.StreamlineCloud;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@Setter
@RequiredArgsConstructor
public class StreamlineCommand {

    @NonNull
    String name;

    @NonNull
    StreamlineCommandTree commandTree;

    StreamlineLogger logger = StreamlineCloud.getLogger();

    public void run(String[] args) {

        StringBuilder commandFromUser = new StringBuilder();
        for (String arg : args) commandFromUser.append(arg).append(" ");

        HashMap<String, Object> context = new HashMap<>();
        AtomicBoolean abort = new AtomicBoolean(false);

        commandTree.checks.forEach((command, execute) -> {
            if (abort.get()) return;
            if (checkPath(command, commandFromUser.toString())) {
                context.putAll(execute.execute(getVariables(args), logger));
                if ((Boolean) context.get("abort")) abort.set(true);
            }
        });

        if (abort.get()) return;

        commandTree.tree.forEach(((subCommand) -> {
            if (checkPath(subCommand.getName(), commandFromUser.toString())) {
                subCommand.execute.execute(getVariables(args), logger);
            }
        }));

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

    public static HashMap<String, Object> abort() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("abort", true);
        return map;
    }

}
