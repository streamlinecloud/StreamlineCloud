package net.streamlinecloud.backend.service

import net.streamlinecloud.api.group.StreamlineGroup
import net.streamlinecloud.api.socket.SocketResponse
import net.streamlinecloud.api.socket.SocketResponseType
import net.streamlinecloud.backend.repository.GroupRepository
import net.streamlinecloud.backend.socket.GroupSocket
import org.springframework.stereotype.Service

@Service
class GroupService(
    private val groupRepository: GroupRepository,
    private val groupSocket: GroupSocket
) {

    fun findAll(): List<StreamlineGroup> = groupRepository.findAll()

    fun update(group: StreamlineGroup): StreamlineGroup {
        val updated = groupRepository.save(group)
        groupSocket.sendToAll(SocketResponse(group, this.javaClass.name, SocketResponseType.UPDATE))
        return updated
    }

}