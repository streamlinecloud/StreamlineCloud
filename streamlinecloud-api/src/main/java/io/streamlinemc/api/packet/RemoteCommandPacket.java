package io.streamlinemc.api.packet;

import lombok.Getter;

@Getter
public class RemoteCommandPacket {

    String command, server, executor;

    public RemoteCommandPacket(String command, String server, String executor) {
        this.command = command;
        this.server = server;
        this.executor = executor;
    }

}
