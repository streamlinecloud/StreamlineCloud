package net.streamlinecloud.backend.repository

import net.streamlinecloud.api.node.StreamlineNode
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import java.util.Optional

interface NodeRepository : JpaRepository<StreamlineNode, Long> {

    fun findByUuid(uuid: String): StreamlineNode?

}