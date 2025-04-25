package net.streamlinecloud.mc.common.core;

import net.streamlinecloud.mc.common.core.manager.AbstractGroupManager;
import net.streamlinecloud.mc.paper.manager.PlayerManager;
import net.streamlinecloud.mc.common.core.manager.AbstractServerManager;
import lombok.Getter;

@Getter
public class StreamlineCloud {

    AbstractServerManager serverManager;
    AbstractGroupManager groupManager;
    PlayerManager playerManager;

    public StreamlineCloud() {
    }
}
