package net.streamlinecloud.mc.common.core.manager;

import net.streamlinecloud.api.group.StreamlineGroup;
import net.streamlinecloud.api.server.StreamlineServer;

import java.util.UUID;

public interface ServerManagerImpl {

    /**
     * Subscribe to all updates of an online server
     * @param server Allows you to su
     */
    void subscribe(StreamlineServer server);

    /**
     * Subscribe to all updates of future servers of an existing group
     * @param groupName The name of the group
     */
    void subscribeToGroup(String groupName);

    /**
     * Upload the data of the local server to the backend
     */
    void uploadServerInfo();

    /**
     * Closes and stops the server
     * @param message This message will be shown to all players on the server
     */
    void closeServer(String message);

    /**
     * Moves all players to a new server and stops the current one
     * @param target The name of the new server
     */
    void moveAllPlayersAndStop(String target);

    /**
     * Logs a message with the logger of the local server software
     * @param message The log message
     */
    void log(String message);

    /**
     * @param uuid The UUID of the server
     * @return The server object if the server exists, otherwise null
     */
    StreamlineServer getServerByUuid(String uuid);

    /**
     * @param name The name of the server
     * @return The server object if the server exists, otherwise null
     */
    StreamlineServer getServerByName(String name);

    /**
     * @param groupName The name of the group
     * @return Returns all servers of the group or just an emtpy array
     */
    StreamlineServer[] getServersByGroup(String groupName);

    /**
     * Sends a message to a mc player
     * @param playerUuid The uuid of the mc profile
     * @param message The raw message
     */
    void sendMessageToPlayer(UUID playerUuid, String message);

    /**
     * Connects a player to another serer
     * @param playerUuid The uuid of the mc profile
     * @param serverId The id of the server
     */
    void connectPlayerToServer(UUID playerUuid, String serverId);

    /**
     * Kicks a player
     * @param playerUuid The uuid of the mc profile
     * @param reason The text that the player sees in the disconnected screen
     */
    void kickPlayer(UUID playerUuid, String reason);

    /**
     * @param server The new server
     */
    void onSubscribedServerUpdated(StreamlineServer server);

    /**
     * @param server The started serer
     */
    void onSubscribedServerStarted(StreamlineServer server);

    /**
     * @param server The stopped server
     */
    void onSubscribedServerStopped(StreamlineServer server);

    /**
     * @return Returns the StreamlineServer object that represents the local server
     */
    StreamlineServer getLocalServerInfo();
}
