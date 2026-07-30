package net.streamlinecloud.api.server;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.streamlinecloud.api.group.StreamlineGroup;

/**
 * Helps to build a StreamlineServer instance ready to pass to the startServer function
 */

@RequiredArgsConstructor
public class StreamlineServerBuilder {

    @NonNull
    StreamlineServer server;

    public StreamlineServerBuilder setGroup(StreamlineGroup group) {
        server.setGroup(group.getName());
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