package io.streamlinemc.mc.api.player;

import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class CloudPlayer {

    Player player;

    public CloudPlayer(Player player) {
        this.player = player;
    }
}
