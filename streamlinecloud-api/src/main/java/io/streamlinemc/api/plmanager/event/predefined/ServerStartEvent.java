package io.streamlinemc.api.plmanager.event.predefined;

import io.streamlinemc.api.plmanager.event.Event;
import io.streamlinemc.api.server.ServerState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class ServerStartEvent extends Event {

    String serverName;
    String serverUuid;
    String serverGroup;
    ServerState serverState;
    boolean staticServer;
    int serverPort;

}
