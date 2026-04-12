package net.streamlinecloud.backend.repository

import net.streamlinecloud.api.node.StreamlineNode
import org.springframework.data.repository.CrudRepository
import java.util.Optional

interface NodeRepository : CrudRepository<StreamlineNode, Long> {
    fun findByUuid(uuid: String): Optional<StreamlineNode>

    fun findByKey(key: String?): StreamlineNode?
}