package net.streamlinecloud.api.server;

/**
 * Defines the state of an StreamlineServer object.
 */
public enum ServerState {

    /**
     * The backend has registered the server.
     * The backend will send the start job for the server to the responsible node.
     */
    PREPARING,

    /**
     * Server is registered and the node has received the start job.
     * The Minecraft-Server-Process is being initialized and started.
     */
    STARTING,

    /**
     * The server is running and online.
     * Players can connect.
     */
    ONLINE,

    /**
     * The server is being shut down or will shut down soon.
     * Players should be quit.
     */
    STOPPING,

    /**
     * The Minecraft-Server-Process exited.
     * The files still exist and can will be deleted.
     */
    DELETING,

    /**
     * The server has encountered a fatal error.
     * Logs should be dumped before deleting.
     */
    ERROR
}
