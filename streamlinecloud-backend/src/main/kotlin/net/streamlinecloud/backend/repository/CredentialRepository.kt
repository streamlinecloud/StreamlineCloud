package net.streamlinecloud.backend.repository

import net.streamlinecloud.backend.entity.Credential
import org.springframework.data.jpa.repository.JpaRepository

interface CredentialRepository : JpaRepository<Credential, Long> {

    fun findByIdentifier(identifier: String): Credential?

    fun findByKey(key: String): Credential?

}