package net.streamlinecloud.backend.service

import net.streamlinecloud.api.node.StreamlineNode
import net.streamlinecloud.backend.entity.Credential
import net.streamlinecloud.backend.repository.CredentialRepository
import net.streamlinecloud.backend.repository.NodeRepository
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import java.util.Optional
import java.util.UUID

@Service
class NodeService(
    private val nodeRepository: NodeRepository,
    private val credentialRepository: CredentialRepository
) {

    fun getAll(): List<StreamlineNode> = nodeRepository.findAll();

    fun setKey(nodeId: String, key: String) {
        credentialRepository.save(Credential(nodeId, key.hash()))
    }

    fun checkKey(nodeId: String, key: String): Boolean {
        return credentialRepository.findByIdentifier(nodeId)?.key == key.hash()
    }

    fun getUuidByKey(key: String): String? {
        return credentialRepository.findByKey(key.hash())?.identifier;
    }

    fun getByUuid(uuid: String): StreamlineNode? {
        return nodeRepository.findByUuid(uuid);
    }

    fun register(displayname: String, isWorker: Boolean, isAdmin: Boolean): Credential {
        val uuid = UUID.randomUUID().toString()
        val credential = Credential(
            uuid,
            (if (isWorker) "node_" else "user_") + generateApiKey(48)
        )

        nodeRepository.save(
            StreamlineNode(
                uuid,
                "Main Node",
                isWorker,
                isAdmin
            )
        )

        setKey(credential.identifier, credential.key)

        return credential
    }

    fun String.hash(): String {
        return MessageDigest
            .getInstance("SHA-256")
            .digest(this.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }

    fun generateApiKey(length: Int = 32): String {
        val random = SecureRandom()
        val bytes = ByteArray(length)
        random.nextBytes(bytes)

        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(bytes)
    }

}