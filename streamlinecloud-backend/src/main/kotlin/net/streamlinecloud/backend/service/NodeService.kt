package net.streamlinecloud.backend.service

import net.streamlinecloud.api.node.StreamlineNode
import net.streamlinecloud.backend.repository.NodeRepository
import org.springframework.stereotype.Service

@Service
class NodeService(
    private val nodeRepository: NodeRepository
) {

    fun getAll(): List<StreamlineNode> = nodeRepository.findAll();

}