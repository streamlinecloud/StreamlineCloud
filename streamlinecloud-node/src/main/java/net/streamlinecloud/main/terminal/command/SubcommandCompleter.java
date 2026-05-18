package net.streamlinecloud.main.terminal.command;

import java.util.List;

public interface SubcommandCompleter {

    List<String> complete();

}
