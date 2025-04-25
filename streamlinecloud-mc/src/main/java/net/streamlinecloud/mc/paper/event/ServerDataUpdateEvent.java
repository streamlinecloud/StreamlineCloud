package net.streamlinecloud.mc.paper.event;

import lombok.Getter;
import net.streamlinecloud.api.server.StreamlineServer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class ServerDataUpdateEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    StreamlineServer server;

    public ServerDataUpdateEvent(StreamlineServer server) {
        this.server = server;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
