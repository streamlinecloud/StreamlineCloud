package io.streamlinemc.api.plmanager.event.predefined;

import io.streamlinemc.api.plmanager.event.Event;
import lombok.*;

@AllArgsConstructor
@Getter @Setter
public class ExecuteCommandEvent extends Event {

    String command;
    String[] args;
    String user;

}
