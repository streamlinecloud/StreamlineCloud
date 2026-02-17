package net.streamlinecloud.backend.repository

import net.streamlinecloud.api.node.StreamlineNode
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import java.util.Optional
import java.util.UUID

interface NodeRepository : CrudRepository<StreamlineNode, Long> {
    fun findByUuid(uuid: String): Optional<StreamlineNode>
}