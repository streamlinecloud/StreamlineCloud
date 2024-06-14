package io.streamlinemc.api.group;

import io.streamlinemc.api.server.ServerRuntime;

import java.util.List;

public interface StreamlineGroupImpl {

    /**
     *
     * Returns name of the Group
     * @return name
     */
    String getName();

    /**
     * Returns the minimal Online Count of the Group
     * @return minCount
     */
    int getMinOnlineCount();

    List<String> getTemplates();

    String getJavaExec();
    ServerRuntime getRuntime();
}
