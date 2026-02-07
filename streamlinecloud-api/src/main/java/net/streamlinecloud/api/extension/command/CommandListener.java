package net.streamlinecloud.api.extension.command;

public interface CommandListener {
    String getName();
    String getDescription();

    void onExecute(String[] args);

}
