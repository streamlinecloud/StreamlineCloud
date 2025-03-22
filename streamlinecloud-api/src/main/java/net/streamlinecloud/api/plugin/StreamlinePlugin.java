package net.streamlinecloud.api.plugin;

import net.streamlinecloud.api.plugin.command.CommandManager;
import net.streamlinecloud.api.plugin.event.EventManager;

public interface StreamlinePlugin {
    void start(EventManager eventManager, CommandManager commandManager);
    void shutdown();

}
