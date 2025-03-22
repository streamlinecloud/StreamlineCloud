package net.streamlinecloud.api.plugin.event.predefined;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.streamlinecloud.api.plugin.event.Event;

@AllArgsConstructor
@Getter @Setter
public class ServerDeleteEvent extends Event {

    String name;
    String uuid;

}
