package net.streamlinecloud.backend.service

import net.streamlinecloud.api.group.StreamlineGroup
import net.streamlinecloud.api.node.StreamlineNode
import net.streamlinecloud.api.server.StreamlineServer
import net.streamlinecloud.api.socket.SocketResponse
import net.streamlinecloud.api.socket.SocketResponseType
import net.streamlinecloud.backend.socket.ServerSocket
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class ServerService(
    private val activeSessionService: ActiveSessionService,
    private val nodeService: NodeService,
    private val groupService: GroupService,
    private val serverSocket: ServerSocket,
) {

    var runningServers: MutableList<StreamlineServer> = ArrayList()

    //TODO: Re-implement restart feature
    var restartingServers: HashMap<StreamlineServer?, StreamlineServer?> =
        HashMap<StreamlineServer?, StreamlineServer?>()

    /**
     * @return The node with the fewest servers online.
     * Has to be in the ServerService because it uses the runningServers List.
     */
    fun findNodeWithFewestServersOnline(nodes: List<StreamlineNode>): StreamlineNode? =
        nodes.stream().min(Comparator.comparingInt{ node -> getNodeOnlineCount(node)}).getOrNull()

    /**
     * Runs findNodeWithFewestServersOnline() but with every online node.
     */
    fun findNodeWithFewestServersOnline(): StreamlineNode? =
        findNodeWithFewestServersOnline(nodeService.getAll())

    /**
     * @return The number of online servers that are online on the given node
     */
    fun getNodeOnlineCount(node: StreamlineNode): Int =
        runningServers.count { server -> server.nodeUuid.equals(node.uuid) }

    /**
     * Adds a new server or updates an existing one
     */
    fun update(server: StreamlineServer): StreamlineServer {
        runningServers.removeIf { it.nodeUuid.equals(server.nodeUuid) }
        runningServers.add(server)
        serverSocket.sendToAll(SocketResponse(server, this.javaClass.name, SocketResponseType.UPDATE))
        return server
    }

    fun getServersByGroup(group: StreamlineGroup): List<StreamlineServer> =
        runningServers.stream().filter{ streamlineServer -> streamlineServer.group.equals(group.name) }.toList()

    fun startServer(server: StreamlineServer) {
        val node: StreamlineNode? = findNodeWithFewestServersOnline(nodeService.findSuitableNodesForGroup(groupService.getByName(server.group)))
        if (node == null) {
            println("Unable to find suitable node for group " + server.group)
        } else {
            println("server start for " + server.group + " node: " + node.displayname)
        }
    }


}