package net.streamlinecloud.api.extension.event.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.streamlinecloud.api.extension.event.Event;
import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.api.server.ServerState;

@AllArgsConstructor
@Getter @Setter
public class ServerPreStartEvent extends Event {

    String name;
    ServerRuntime serverRuntime;
    String uuid;
    ServerState serverState;

}
