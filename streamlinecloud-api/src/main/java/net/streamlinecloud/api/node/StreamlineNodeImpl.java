package net.streamlinecloud.api.node;

import java.util.Map;
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
     * @return Returns the api key for the node.
     * The boolean defines if the key is hashed or not.
     */
    Map<Boolean, String> getKey();
}
