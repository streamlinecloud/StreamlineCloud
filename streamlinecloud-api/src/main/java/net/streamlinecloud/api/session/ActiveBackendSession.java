package net.streamlinecloud.api.session;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class ActiveBackendSession {

    UUID uuid = UUID.randomUUID();

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


}
