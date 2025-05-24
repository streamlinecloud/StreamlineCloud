package net.streamlinecloud.api.server.impl;

public interface StaticStreamlineServerDataImpl {

    /**
     * @return The name of the Server (not unique)
     */
    String getName();

    /**
     * @return Paper / Velocity server port
     */
    int getPort();

    /**
     * @return Stop the server automatically at a specific date. (In timeMillis) (-1 = disabled)
     */
    long getStopTime();

    /**
     * @return Paper / Velocity server ip (probably localhost)
     */
    String getIp();

    /**
     * @return Get the name of the group
     */
    String getGroup();

    /**
     * @return Unique server id (name + uuid = full server name)
     */
    String getUuid();

}
