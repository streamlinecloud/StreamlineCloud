package io.streamlinemc.api.plmanager.command;

import org.jetbrains.annotations.NotNull;

public interface CommandListener {
    @NotNull
    String getName();
    @NotNull
    String getDescription();

    void onExecute(String[] args);

}
