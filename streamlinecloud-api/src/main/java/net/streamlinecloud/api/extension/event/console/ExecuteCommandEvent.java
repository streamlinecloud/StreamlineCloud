package net.streamlinecloud.api.extension.event.console;

import net.streamlinecloud.api.extension.event.Event;
import lombok.*;

@AllArgsConstructor
@Getter @Setter
public class ExecuteCommandEvent extends Event {

    String command;
    String[] args;
    String user;

}
