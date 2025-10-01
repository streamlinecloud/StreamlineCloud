package net.streamlinecloud.main.utils;

import lombok.Getter;
import net.streamlinecloud.api.player.StreamlinePlayer;

import java.util.HashMap;
import java.util.UUID;

/**
 * This class caches which players are online on which server
 */

@Getter
public class PlayerRegister {

    private final HashMap<UUID, StreamlinePlayer> playerRegister = new HashMap<>();
    private PlayerRegister() {}

    private static class Holder {
        private static final PlayerRegister INSTANCE = new PlayerRegister();
    }

    public static PlayerRegister getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * @param uuid The UUID of the Minecraft profile
     * @param player The StreamlinePlayer object that represents the Minecraft player
     */
    public void set(UUID uuid, StreamlinePlayer player) {
        if (playerRegister.containsKey(uuid)) {
            playerRegister.replace(uuid, player);
            return;
        }
        playerRegister.putIfAbsent(uuid, player);
    }

    /**
     * @param uuid The UUID of the Minecraft profile
     * @return {@link StreamlinePlayer StreamlinePlayer} or null if not found.
     */
    public StreamlinePlayer get(UUID uuid) {
        return playerRegister.get(uuid);
    }

    /**
     * @param name The name of the Minecraft profile
     * @return {@link StreamlinePlayer StreamlinePlayer} or null if not found.
     */
    public StreamlinePlayer get(String name) {
        for (StreamlinePlayer player : playerRegister.values()) {
            if (player.getName().equals(name)) return player;
        }
        return null;
    }

    /**
     * @param uuid The UUID of the Minecraft profile
     */
    public void delete(UUID uuid) {
        playerRegister.remove(uuid);
    }

}
