package net.streamlinecloud.api.node;

import java.util.UUID;

public interface StreamlineNodeImpl {

    /**
     * @return The unique identifier of the node
     */
    UUID getUuid();

    /**
     * @return Returns if the node is the main node.
     * Only one main node can exist in each setup.
     */
    boolean isMain();

    /**
     * @return The displayname
     * Only for convenience. This does not work as an identifier.
     */
    String getDisplayname();
}
