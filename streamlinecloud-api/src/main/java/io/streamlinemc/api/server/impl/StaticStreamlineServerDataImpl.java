package io.streamlinemc.api.server.impl;

import io.streamlinemc.api.group.StreamlineGroupImpl;

public interface StaticStreamlineServerDataImpl {

    String getName();
    int getPort();
    String getIp();
    String getGroup();
    String getUuid();

}
