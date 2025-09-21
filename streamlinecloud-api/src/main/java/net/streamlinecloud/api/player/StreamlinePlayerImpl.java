package net.streamlinecloud.api.player;

import java.util.UUID;

/**
 * This class represents a Minecraft player who is currently online
 */
public interface StreamlinePlayerImpl {

    /**
     * @return Returns the Minecraft account UUID of the Player.
     */
    UUID getUuid();

    /**
     * @return Returns the current Minecraft username of the player
     */
    String getName();

    /**
     * @return Returns the ID of the current proxy server of the player
     */
    String getCurrentProxy();

    /**
     * @param currentProxyId This has to be a valid Server ID
     */
    void setCurrentProxy(String currentProxyId);

    /**
     * @return Returns the ID of the current server of the player
     */
    String getCurrentServerId();

    /**
     * @param currentServerId This has to be a valid Server ID
     */
    void setCurrentServerId(String currentServerId);



}
