package io.streamlinemc.mc.api;

import io.streamlinemc.api.server.ServerRuntime;
import io.streamlinemc.mc.api.group.GroupManager;
import io.streamlinemc.mc.api.player.PlayerManager;
import io.streamlinemc.mc.api.server.ServerManager;
import io.streamlinemc.mc.utils.StaticCache;
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
