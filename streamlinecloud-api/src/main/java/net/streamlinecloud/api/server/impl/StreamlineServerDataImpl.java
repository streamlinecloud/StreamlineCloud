package net.streamlinecloud.api.server.impl;

import net.streamlinecloud.api.server.ServerState;
import net.streamlinecloud.api.server.ServerUseState;

import java.util.HashMap;
import java.util.UUID;

public interface StreamlineServerDataImpl {

    /**
     * @return Current online players (Minecraft UUID, Minecraft Name)
     */
    HashMap<UUID, String> getOnlinePlayers();

    /**
     * @return Defines what the server is currently used for
     */
    ServerUseState getServerUseState();

    /**
     * @return Defines the current state of the server
     */
    ServerState getServerState();


}
