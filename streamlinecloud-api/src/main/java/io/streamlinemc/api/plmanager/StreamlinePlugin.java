package io.streamlinemc.api.plmanager;


import io.streamlinemc.api.plmanager.command.CommandManager;
import io.streamlinemc.api.plmanager.event.EventManager;

import java.util.List;

public interface StreamlinePlugin {
    void start(EventManager eventManager, CommandManager commandManager);
    void shutdown();

}
