package net.streamlinecloud.api.plugin.event.predefined;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.streamlinecloud.api.plugin.event.Event;
import net.streamlinecloud.api.server.ServerState;

@AllArgsConstructor
@Getter @Setter
public class ServerStopEvent extends net.streamlinecloud.api.plugin.event.Event {
    String serverName;
    String serverUuid;
    String serverGroup;
    net.streamlinecloud.api.server.ServerState serverState;
    boolean staticServer;
    int serverPort;
}
