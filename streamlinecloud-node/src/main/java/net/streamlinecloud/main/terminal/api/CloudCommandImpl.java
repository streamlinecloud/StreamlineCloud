package net.streamlinecloud.main.terminal.api;

import java.net.MalformedURLException;
import java.util.List;

public interface CloudCommandImpl {

    String name();

    String description();

    String[] aliases();

    List<CloudCommandArgument> arguments();

    void execute(String[] args);
}
