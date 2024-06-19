package io.streamlinemc.api.plmanager.event.predefined;

import io.streamlinemc.api.plmanager.event.Event;
import lombok.*;

@AllArgsConstructor
@Getter @Setter
public class ConsoleMessageEvent extends Event {
    String message;
}
