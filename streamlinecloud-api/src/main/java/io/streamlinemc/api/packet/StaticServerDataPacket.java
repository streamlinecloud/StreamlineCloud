package io.streamlinemc.api.packet;

import io.streamlinemc.api.group.StreamlineGroupImpl;
import io.streamlinemc.api.server.impl.StaticStreamlineServerDataImpl;
import lombok.Getter;

@Getter
public class StaticServerDataPacket implements StaticStreamlineServerDataImpl {

    String name;
    int port;
    String ip;
    String group;
    String uuid;

    public StaticServerDataPacket(String name, int port, String ip, String group, String uuid) {
        this.name = name;
        this.port = port;
        this.ip = ip;
        this.group = group;
        this.uuid = uuid;
    }
}
