package net.streamlinecloud.main.terminal.command;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CommandManager {

    @Getter
    private static CommandManager instance;

    List<StreamlineCommand> commandMap = new ArrayList<>();

    public CommandManager() {
        instance = this;
    }
}
