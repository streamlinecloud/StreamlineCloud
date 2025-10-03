package net.streamlinecloud.main.core.group;

import lombok.Getter;
import net.streamlinecloud.main.core.server.RunningServer;
import net.streamlinecloud.main.core.server.RunningServerManager;
import net.streamlinecloud.main.utils.Cache;

import java.util.ArrayList;
import java.util.List;

public class CloudGroupManager {

    @Getter
    private static CloudGroupManager instance;

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

    public CloudGroup getGroupByName(String name) {

        for (CloudGroup group : Cache.i().getActiveGroups()) {

            if (group.getName().equals(name)) {

                return group;
            }
        }
        return null;
    }

    public boolean groupExists(String name) {
        for (CloudGroup group : Cache.i().getActiveGroups()) {
            if (group.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
