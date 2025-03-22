package net.streamlinecloud.mc.api;

import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.mc.api.group.GroupManager;
import net.streamlinecloud.mc.api.player.PlayerManager;
import net.streamlinecloud.mc.api.server.ServerManager;
import net.streamlinecloud.mc.utils.StaticCache;
import lombok.Getter;

@Getter
public class StreamlineCloud {

    ServerManager serverManager;
    GroupManager groupManager;
    PlayerManager playerManager;

    public StreamlineCloud() {
        if (!StaticCache.getRuntime().equals(ServerRuntime.SERVER)) return;
        this.serverManager = new ServerManager();
        this.groupManager = new GroupManager();
        this.playerManager = new PlayerManager();
    }
}
