package net.streamlinecloud.api.extension.event.console;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.streamlinecloud.api.extension.event.Event;

@AllArgsConstructor
@Getter @Setter
public class ConsoleInputEvent extends Event {
    String input;
}
