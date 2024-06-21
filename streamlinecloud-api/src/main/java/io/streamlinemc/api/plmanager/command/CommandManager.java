package io.streamlinemc.api.plmanager.command;

import io.streamlinemc.api.exceptions.CommandException;

import java.lang.reflect.Method;
import java.util.HashMap;

public class CommandManager {

    public final HashMap<String, CommandListener> commands = new HashMap<>();

    public void registerCommand(CommandListener commandListener) throws CommandException {

        if (commands.containsKey(commandListener.getName())) {
            throw new CommandException("Command with name " + commandListener.getName() + " already exists");
        }

        commands.put(commandListener.getName(), commandListener);
    }

    public void unregisterCommand(String name) {
        commands.remove(name);
    }

    public void executeCommand(String name, String[] args) throws CommandException {
        if (!commands.containsKey(name)) {
            return;
        }

        CommandListener listener = commands.get(name);
        listener.onExecute(args);

    }
}
