package io.streamlinemc.api.server.impl;

import io.streamlinemc.api.group.StreamlineGroupImpl;
import io.streamlinemc.api.server.ServerState;
import io.streamlinemc.api.server.ServerUseState;

import java.util.HashMap;
import java.util.UUID;

public interface StreamlineServerImpl {

    String getName();
    int getPort();
    String getIp();
    StreamlineGroupImpl getGroup();
    String getUuid();
    HashMap<UUID, String> getOnlinePlayers();
    ServerUseState getServerUseState();
    ServerState getServerState();
}
