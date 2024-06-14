package io.streamlinemc.api.server.impl;

import io.streamlinemc.api.server.ServerState;
import io.streamlinemc.api.server.ServerUseState;

import java.util.HashMap;
import java.util.UUID;

public interface StreamlineServerDataImpl {

    HashMap<UUID, String> getOnlinePlayers();
    ServerUseState getServerUseState();
    ServerState getServerState();


}
