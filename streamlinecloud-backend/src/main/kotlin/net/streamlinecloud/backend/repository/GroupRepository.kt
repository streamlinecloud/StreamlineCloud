package net.streamlinecloud.backend.repository

import net.streamlinecloud.api.group.StreamlineGroup
import org.springframework.data.jpa.repository.JpaRepository

interface GroupRepository : JpaRepository<StreamlineGroup, Long>