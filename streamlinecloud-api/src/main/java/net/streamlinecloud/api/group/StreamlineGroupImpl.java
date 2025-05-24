package net.streamlinecloud.api.group;

import net.streamlinecloud.api.server.ServerRuntime;

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

    /**
     * @return Automatically start the server overflow process
     */
    int getOverflowMinutes();

    /**
     * @return The path to the template (%default for the default path from the main config)
     */
    List<String> getTemplates();

    /**
     * @return The path to the java executable
     */
    String getJavaExec();

    /**
     * @return The type of the server (SERVER / PROXY / ETC)
     */
    ServerRuntime getRuntime();
}
