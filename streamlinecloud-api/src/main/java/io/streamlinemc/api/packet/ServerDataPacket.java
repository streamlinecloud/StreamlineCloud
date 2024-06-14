package io.streamlinemc.api.packet;

import io.streamlinemc.api.server.ServerState;
import io.streamlinemc.api.server.ServerUseState;
import io.streamlinemc.api.server.impl.StreamlineServerDataImpl;
import lombok.Getter;

import java.util.HashMap;
import java.util.UUID;

@Getter
public class ServerDataPacket implements StreamlineServerDataImpl {

    HashMap<UUID, String> onlinePlayers;
    ServerUseState serverUseState;
    ServerState serverState;

    public ServerDataPacket(HashMap<UUID, String> onlinePlayers, ServerUseState serverUseState, ServerState serverState) {
        this.onlinePlayers = onlinePlayers;
        this.serverUseState = serverUseState;
        this.serverState = serverState;
    }
}
