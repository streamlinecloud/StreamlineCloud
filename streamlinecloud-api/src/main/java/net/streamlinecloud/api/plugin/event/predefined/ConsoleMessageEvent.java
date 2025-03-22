package net.streamlinecloud.api.plugin.event.predefined;
import net.streamlinecloud.api.plugin.event.Event;
import net.streamlinecloud.api.server.ServerState;
import lombok.*;

@AllArgsConstructor
@Getter @Setter
public class ConsoleMessageEvent extends Event {
    String message;
}
