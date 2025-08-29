package net.streamlinecloud.mc.common.core.manager;

import net.streamlinecloud.api.server.StreamlineServer;

public interface ServerManagerImpl {

    void subscribe(StreamlineServer server);
    void subscribeToGroup(String name);
    void uploadServerInfo();
    void closeServer(String message);
    void moveAllPlayersAndStop(String target);
    void log(String message);
    StreamlineServer getServerByUuid(String uuid);
    StreamlineServer getServerByName(String name);

    void onSubscribedServerUpdated(StreamlineServer server);
    void onSubscribedServerStarted(StreamlineServer server);
    void onSubscribedServerStopped(StreamlineServer server);

    StreamlineServer getLocalServerInfo();
}
