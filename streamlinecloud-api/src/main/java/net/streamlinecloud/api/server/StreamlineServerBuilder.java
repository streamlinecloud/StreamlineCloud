package net.streamlinecloud.api.server;

import net.streamlinecloud.api.group.StreamlineGroup;

import java.util.UUID;

/**
 * Helps to build a StreamlineServer instance ready to pass to the startServer function
 */

public class StreamlineServerBuilder {

    StreamlineServer server = new StreamlineServer();

    public StreamlineServerBuilder setGroup(StreamlineGroup group) {
        server.setUuid(UUID.randomUUID().toString());
        server.setGroup(group.getName());
        server.setName(server.group + server.uuid);
        return this;
    }

    public StreamlineServer toStreamlineServer() {
        return build();
    }

    public StreamlineServer build() {

        if (server.getGroup().equals("WITHOUT") || server.getGroup().isEmpty()) {
            server.getAdditionalTemplates().add("server/empty");
        }

        return server;
    }
}