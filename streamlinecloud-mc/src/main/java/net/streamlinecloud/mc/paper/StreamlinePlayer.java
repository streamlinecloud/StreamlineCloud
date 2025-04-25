package net.streamlinecloud.mc.paper;

import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class StreamlinePlayer {

    Player player;

    public StreamlinePlayer(Player player) {
        this.player = player;
    }
}
