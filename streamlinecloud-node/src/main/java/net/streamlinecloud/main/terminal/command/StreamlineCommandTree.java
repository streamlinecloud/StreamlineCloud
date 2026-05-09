package net.streamlinecloud.main.terminal.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StreamlineCommandTree {

    List<StreamlineSubcommand> tree = new ArrayList<>();
    HashMap<String, SubcommandCheckExecute> checks = new HashMap<>();

    public StreamlineCommandTree add(StreamlineSubcommand streamlineSubcommand) {
        streamlineSubcommand.setCommandTree(this);
        tree.add(streamlineSubcommand);
        return this;
    }

    public StreamlineCommandTree addCheck(String command, SubcommandCheckExecute execute) {
        checks.put(command, execute);
        return this;
    }

}
