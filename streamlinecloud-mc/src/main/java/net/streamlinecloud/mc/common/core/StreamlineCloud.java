package net.streamlinecloud.mc.common.core;

import net.streamlinecloud.mc.common.core.manager.AbstractGroupManager;
import net.streamlinecloud.mc.paper.manager.PaperPlayerManager;
import net.streamlinecloud.mc.common.server.AbstractServerManager;
import lombok.Getter;

@Getter
public class StreamlineCloud {

    AbstractServerManager serverManager;
    AbstractGroupManager groupManager;
    PaperPlayerManager playerManager;

    public StreamlineCloud() {
    }
}
