package net.streamlinecloud.backend.service

import net.streamlinecloud.api.node.StreamlineNode
import net.streamlinecloud.api.server.StreamlineServer
import org.springframework.stereotype.Service

@Service
class ServerService(
    private val activeSessionService: ActiveSessionService,
    private val nodeService: NodeService,
    private val groupService: GroupService,
) {

    var onlineServers: MutableList<StreamlineServer> =
        ArrayList<StreamlineServer>()
    var serversWaitingForStart: MutableList<StreamlineServer> =
        ArrayList<StreamlineServer>()

    //TODO: Re-implement restart feature
    var restartingServers: HashMap<StreamlineServer?, StreamlineServer?> =
        HashMap<StreamlineServer?, StreamlineServer?>()

    /**
     * @return The node with the fewest servers online.
     */
    fun findNodeWithLeastServersOnline(nodes: List<StreamlineNode>): StreamlineNode =
        nodes.stream().min(Comparator.comparingInt{ node -> getNodeOnlineCount(node)}).get()

    /**
     * Runs findNodeWithLeastServersOnline() but with every online node.
     */
    fun findNodeWithLeastServersOnline(): StreamlineNode =
        findNodeWithLeastServersOnline(nodeService.getAll())

    fun getNodeOnlineCount(node: StreamlineNode): Int =
        onlineServers.count { server -> server.nodeUuid.equals(node.uuid) }

}