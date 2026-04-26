package net.streamlinecloud.main.core.group;

import lombok.Getter;
import lombok.Setter;
import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.api.terminal.ReplacePaket;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.server.RunningServer;
import net.streamlinecloud.main.core.server.RunningServerManager;
import net.streamlinecloud.main.utils.Cache;
import net.streamlinecloud.main.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

@Getter
public class CloudGroupManager {

    @Getter
    private static CloudGroupManager instance;

    final PriorityQueue<CloudGroup> activeGroups = new PriorityQueue<>(
            Comparator.comparingInt(CloudGroup::getPriority).reversed()
    );

    @Setter
    CloudGroup defaultGroup;

    public CloudGroupManager() {
        instance = this;
    }

    public List<RunningServer> getGroupOnlineServers(CloudGroup g) {
        List<RunningServer> onlineServers = new ArrayList<>();
        for (RunningServer s : RunningServerManager.getInstance().getRunningServers()) {
            if (s.getGroup().equals(g.getName())) {
                onlineServers.add(s);
            }
        }
        return onlineServers;
    }
}
