package net.streamlinecloud.mc.common.core.manager;

import net.streamlinecloud.api.server.StreamlineServer;

import java.util.UUID;

public interface ServerManagerImpl {

    void subscribe(StreamlineServer server);
    void subscribeToGroup(String name);
    void uploadServerInfo();
    StreamlineServer getServer(UUID uuid);
    StreamlineServer getServer(String name);

    void onSubscribedServerUpdated(StreamlineServer server);
    void onSubscribedServerStarted(StreamlineServer server);
    void onSubscribedServerStopped(StreamlineServer server);

    StreamlineServer getLocalServerInfo();
}
