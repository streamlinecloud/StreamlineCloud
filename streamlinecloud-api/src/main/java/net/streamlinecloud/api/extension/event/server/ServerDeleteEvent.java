package net.streamlinecloud.api.extension.event.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.streamlinecloud.api.extension.event.Event;

@AllArgsConstructor
@Getter @Setter
public class ServerDeleteEvent extends Event {

    String name;
    String uuid;

}
