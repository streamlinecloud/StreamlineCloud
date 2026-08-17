package net.streamlinecloud.backend.task

import net.streamlinecloud.api.group.StreamlineGroup
import net.streamlinecloud.api.server.StreamlineServerBuilder
import net.streamlinecloud.backend.service.GroupService
import net.streamlinecloud.backend.service.ServerService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.*
import java.util.stream.Collectors

@Component
class ServerStarter(
    private val serverService: ServerService,
    private val groupService: GroupService
) {

    @Scheduled(fixedRate = 20_000)
    fun task() {
        startMissingServers()
    }

    /**
     * Executes [.startMissingServers] for every active group.
     * Groups are processed by priority.
     */
    fun startMissingServers() {
        val groupsWaiting = groupService.findAll().stream()
            .filter { group -> group.minOnlineCount > serverService.getServersByGroup(group).size }.collect(Collectors.toList())

        if (groupsWaiting.isEmpty()) return

        val queue = PriorityQueue<StreamlineGroup>(compareByDescending { it.priority })
            .apply { addAll(groupsWaiting) }
            .let { pq -> List(pq.size) { pq.poll() } }

        for (group in queue) {
            startMissingServers(group)
        }

    }

    /**
     * Starts all servers needed to reach the minOnlineCount of the group
     *
     * @param group The target group
     */
    fun startMissingServers(group: StreamlineGroup) {
        var serversNeeded: Int = group.minOnlineCount - serverService.getServersByGroup(group).size
        while (serversNeeded > 0) {
            serverService.startServer(StreamlineServerBuilder().setGroup(group).build())
            serversNeeded--
        }
    }


}