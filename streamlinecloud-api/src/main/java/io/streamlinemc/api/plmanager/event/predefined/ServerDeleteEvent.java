package io.streamlinemc.api.plmanager.event.predefined;

import io.streamlinemc.api.plmanager.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class ServerDeleteEvent extends Event {

    String name;
    String uuid;

}
