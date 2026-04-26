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
    int getAutoRestartMinutes();

    /**
     * @return The path to the template (%default for the default path from the main config)
     */
    List<String> getTemplates();

    /**
     * @return The name of the server software for this group that is installed into StreamlineCloud
     */
    String getSoftwareName();

    /**
     * @return The path to the java executable
     */
    String getJavaExec();

    /**
     * @return The type of the server (SERVER / PROXY / ETC)
     */
    ServerRuntime getRuntime();

    /**
     * @return The priority as int. lesser important (low int) -> higher important (high int)
     */
    int getPriority();

    /**
     * @return The priority as int. lesser important (low int) -> higher important (high int)
     */
    int getMinimumHeap();

    /**
     * @return The priority as int. lesser important (low int) -> higher important (high int)
     */
    int getMaximumHeap();
}
