package net.streamlinecloud.mc.core.player;

import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class CloudPlayer {

    Player player;

    public CloudPlayer(Player player) {
        this.player = player;
    }
}
