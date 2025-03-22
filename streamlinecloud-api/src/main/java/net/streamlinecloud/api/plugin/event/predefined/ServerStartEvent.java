package net.streamlinecloud.api.plugin.event.predefined;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.streamlinecloud.api.plugin.event.Event;
import net.streamlinecloud.api.server.ServerState;

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
