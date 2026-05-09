package net.streamlinecloud.main.terminal.command;

import net.streamlinecloud.api.terminal.StreamlineLogger;

import java.util.HashMap;
import java.util.Objects;

public interface SubcommandCheckExecute {

    HashMap<String, Object> execute(HashMap<String, String> variables, StreamlineLogger logger);

}
