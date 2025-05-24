package net.streamlinecloud.main.core.group;

import lombok.Getter;
import net.streamlinecloud.main.core.server.CloudServer;
import net.streamlinecloud.main.utils.Cache;

import java.util.ArrayList;
import java.util.List;

public class CloudGroupManager {

    @Getter
    private static CloudGroupManager instance;

    public CloudGroupManager() {
        instance = this;
    }

    public List<CloudServer> getGroupOnlineServers(CloudGroup g) {
        List<CloudServer> onlineServers = new ArrayList<>();
        for (CloudServer s : Cache.i().getRunningServers()) {
            if (s.getGroup().equals(g.getName())) {
                onlineServers.add(s);
            }
        }
        return onlineServers;
    }

    public CloudGroup getGroupByName(String name) {

        for (CloudGroup group : Cache.i().getActiveGroups()) {

            if (group.getName().equals(name)) {

                return group;
            }
        }
        return null;
    }
}
