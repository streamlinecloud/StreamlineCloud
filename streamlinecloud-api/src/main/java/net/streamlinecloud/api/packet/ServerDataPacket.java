package net.streamlinecloud.api.packet;

import lombok.Getter;
import net.streamlinecloud.api.server.ServerState;
import net.streamlinecloud.api.server.ServerUseState;
import net.streamlinecloud.api.server.impl.StreamlineServerDataImpl;

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
