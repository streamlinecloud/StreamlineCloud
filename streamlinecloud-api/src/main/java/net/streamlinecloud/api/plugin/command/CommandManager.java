package net.streamlinecloud.api.plugin.command;

import net.streamlinecloud.api.exceptions.CommandException;

import java.lang.reflect.Method;
import java.util.HashMap;

public class CommandManager {

    public final HashMap<String, CommandListener> commands = new HashMap<>();

    /**
     * Register a command
     *
     * @param commandListener Command Listener to register
     * @throws CommandException If a command with the same name already exists
     */
    public void registerCommand(CommandListener commandListener) throws CommandException {

        if (commands.containsKey(commandListener.getName())) {
            throw new CommandException("Command with name " + commandListener.getName() + " already exists");
        }

        commands.put(commandListener.getName(), commandListener);
    }

    /**
     * Unregisters a command
     * @param name Name of the Command
     */
    public void unregisterCommand(String name) {
        commands.remove(name);
    }

    /**
     * Execute a command
     *
     * @param name Name of the command
     * @param args Arguments for the command
     * @throws CommandException If the command does not exist
     */
    public void executeCommand(String name, String[] args) throws CommandException {
        if (!commands.containsKey(name)) {
            return;
        }

        CommandListener listener = commands.get(name);
        listener.onExecute(args);

    }
}
