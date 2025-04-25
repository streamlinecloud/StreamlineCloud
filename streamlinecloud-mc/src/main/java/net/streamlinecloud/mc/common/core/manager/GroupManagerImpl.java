package net.streamlinecloud.mc.common.core.manager;

import net.streamlinecloud.api.group.StreamlineGroup;

public interface GroupManagerImpl {

    void updateGroups();
    void onUpdate(StreamlineGroup group);
    boolean groupExists(String name);
    StreamlineGroup getGroup(String name);
}
