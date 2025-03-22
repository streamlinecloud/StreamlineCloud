package net.streamlinecloud.api.plugin.event.predefined;

import net.streamlinecloud.api.plugin.event.Event;
import lombok.*;

@AllArgsConstructor
@Getter @Setter
public class ExecuteCommandEvent extends Event {

    String command;
    String[] args;
    String user;

}
