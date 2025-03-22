package net.streamlinecloud.api.server;

import lombok.Getter;
import lombok.Setter;
import net.streamlinecloud.api.server.impl.StaticStreamlineServerDataImpl;
import net.streamlinecloud.api.server.impl.StreamlineServerDataImpl;

import java.util.HashMap;
import java.util.UUID;

@Getter
@Setter
public class StreamlineServer implements StreamlineServerDataImpl, StaticStreamlineServerDataImpl {

    String name = "unknown";
    String ip = "localhost";

    int port = 1;
    int maxOnlineCount = -1;

    HashMap<UUID, String> onlinePlayers = new HashMap<>();
    ServerUseState serverUseState = ServerUseState.UNKNOWN;
    ServerState serverState = ServerState.PREPARING;
    String group = "WITHOUT";
    String uuid = "000a0000-a00a-00a0-a000-000000000000";
    String rconUuid = UUID.randomUUID().toString();
    ServerRuntime runtime = ServerRuntime.SERVER;

}
