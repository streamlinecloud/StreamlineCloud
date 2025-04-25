package net.streamlinecloud.api.extension.event.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.streamlinecloud.api.extension.event.Event;
import net.streamlinecloud.api.server.ServerState;

@AllArgsConstructor
@Getter @Setter
public class OutgoingServerMessageEvent extends Event {

    String serverName;
    String serverUuid;
    String serverGroup;
    ServerState serverState;
    boolean staticServer;
    int serverPort;
    String message;

}
