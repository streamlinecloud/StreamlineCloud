package net.streamlinecloud.main.terminal.command;

import net.streamlinecloud.api.terminal.StreamlineLogger;

import java.util.HashMap;

public interface SubcommandExecute {

    void execute(HashMap<String, String> variables, StreamlineLogger logger);

}
