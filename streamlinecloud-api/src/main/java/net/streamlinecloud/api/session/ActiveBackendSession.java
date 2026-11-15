package net.streamlinecloud.api.session;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter @Setter
@RequiredArgsConstructor
public class ActiveBackendSession {

    /**
     * The id of the socket session
     */
    @NonNull
    String sessionId;

    /**
     * The object attached to the session could be a node or a user.
     * Node ids start with "node_" and user ids with "user_".
     */
    @NonNull
    String childId;

    /**
     * The templates that can be provided by the node.
     */
    List<String> templates = new ArrayList<>();

    /**
     * All online servers. Includes the starting servers.
     */
    List<String> onlineServers = new ArrayList<>();

    /**
     * The last time the node sent a heartbeat to the server.
     */
    long lastHeartbeat = System.currentTimeMillis();


}
