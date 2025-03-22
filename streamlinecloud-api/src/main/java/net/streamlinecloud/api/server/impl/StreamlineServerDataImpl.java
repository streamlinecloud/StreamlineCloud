package net.streamlinecloud.api.server.impl;

import net.streamlinecloud.api.server.ServerState;
import net.streamlinecloud.api.server.ServerUseState;

import java.util.HashMap;
import java.util.UUID;

public interface StreamlineServerDataImpl {

    HashMap<UUID, String> getOnlinePlayers();
    ServerUseState getServerUseState();
    ServerState getServerState();


}
