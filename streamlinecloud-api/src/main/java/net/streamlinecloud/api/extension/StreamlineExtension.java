package net.streamlinecloud.api.extension;

import net.streamlinecloud.api.extension.command.CommandManager;
import net.streamlinecloud.api.extension.event.EventManager;

public interface StreamlineExtension {

    void initialize(EventManager eventManager, CommandManager commandManager);
    void disable();

}
