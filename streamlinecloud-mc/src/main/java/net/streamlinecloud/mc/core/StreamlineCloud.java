package net.streamlinecloud.mc.core;

import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.mc.core.group.GroupManager;
import net.streamlinecloud.mc.core.player.PlayerManager;
import net.streamlinecloud.mc.core.server.ServerManager;
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
