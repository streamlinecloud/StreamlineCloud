package net.streamlinecloud.mc.core.event;

import net.streamlinecloud.api.server.StreamlineServer;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ServerDataReceivedEvent extends ServerDataUpdateEvent {

    public ServerDataReceivedEvent(StreamlineServer server) {
        super(server);
    }

}
