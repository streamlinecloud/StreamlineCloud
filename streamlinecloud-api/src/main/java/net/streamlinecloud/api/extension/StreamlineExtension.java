package net.streamlinecloud.api.extension;

import net.streamlinecloud.api.extension.command.CommandManager;
import net.streamlinecloud.api.extension.event.EventManager;

import java.io.File;

public interface StreamlineExtension {

    void initialize(EventManager eventManager, CommandManager commandManager, File dataFolder);
    void disable();

}
