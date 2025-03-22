package net.streamlinecloud.api.packet;

import lombok.Getter;
import net.streamlinecloud.api.server.impl.StaticStreamlineServerDataImpl;

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
