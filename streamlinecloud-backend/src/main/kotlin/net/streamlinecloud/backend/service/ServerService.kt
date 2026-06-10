package net.streamlinecloud.backend.service

import net.streamlinecloud.api.node.StreamlineNode
import net.streamlinecloud.api.server.StreamlineServer
import net.streamlinecloud.api.socket.SocketResponse
import net.streamlinecloud.api.socket.SocketResponseType
import net.streamlinecloud.backend.socket.ServerSocket
import org.springframework.stereotype.Service

@Service
class ServerService(
    private val activeSessionService: ActiveSessionService,
    private val nodeService: NodeService,
    private val groupService: GroupService,
    private val serverSocket: ServerSocket,
) {

    var onlineServers: MutableList<StreamlineServer> =
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

    /**
     * @return The number of online servers that are online on the given node
     */
    fun getNodeOnlineCount(node: StreamlineNode): Int =
        onlineServers.count { server -> server.nodeUuid.equals(node.uuid) }

    /**
     * Adds a new server or updates an existing one
     */
    fun update(server: StreamlineServer): StreamlineServer {
        onlineServers.removeIf { it.nodeUuid.equals(server.nodeUuid) }
        onlineServers.add(server)
        serverSocket.sendToAll(SocketResponse(server, this.javaClass.name, SocketResponseType.UPDATE))
        return server
    }

}