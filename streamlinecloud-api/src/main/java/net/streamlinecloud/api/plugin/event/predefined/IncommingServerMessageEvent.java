package net.streamlinecloud.api.plugin.event.predefined;

import net.streamlinecloud.api.plugin.event.Event;
import net.streamlinecloud.api.server.ServerState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class IncommingServerMessageEvent extends Event {

    String serverName;
    String serverUuid;
    String serverGroup;
    ServerState serverState;
    boolean staticServer;
    int serverPort;
    String message;

}
