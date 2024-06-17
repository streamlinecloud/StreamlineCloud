package io.streamlinemc.api.plmanager.event.predefined;

import io.streamlinemc.api.plmanager.event.Event;
import io.streamlinemc.api.server.ServerRuntime;
import io.streamlinemc.api.server.ServerState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter @Setter
public class ServerPreStartEvent extends Event {

    String name;
    ServerRuntime serverRuntime;
    String uuid;
    ServerState serverState;

}
