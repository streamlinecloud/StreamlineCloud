package net.streamlinecloud.mc.paper.event;

import net.streamlinecloud.api.server.StreamlineServer;

public class ServerDataReceivedEvent extends ServerDataUpdateEvent {

    public ServerDataReceivedEvent(StreamlineServer server) {
        super(server);
    }

}
