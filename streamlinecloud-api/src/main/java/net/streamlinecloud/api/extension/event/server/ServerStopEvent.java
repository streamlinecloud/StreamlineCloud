package net.streamlinecloud.api.extension.event.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.streamlinecloud.api.extension.event.Event;

@AllArgsConstructor
@Getter @Setter
public class ServerStopEvent extends Event {
    String serverName;
    String serverUuid;
    String serverGroup;
    net.streamlinecloud.api.server.ServerState serverState;
    boolean staticServer;
    int serverPort;
}
