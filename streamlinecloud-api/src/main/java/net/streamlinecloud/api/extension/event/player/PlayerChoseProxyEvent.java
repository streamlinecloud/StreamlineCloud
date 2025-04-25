package net.streamlinecloud.api.extension.event.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.streamlinecloud.api.extension.event.Event;
import net.streamlinecloud.api.server.StreamlineServer;

@AllArgsConstructor
@Getter @Setter
public class PlayerChoseProxyEvent extends Event {

    StreamlineServer target;
}
