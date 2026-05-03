package net.streamlinecloud.backend.controller

import net.streamlinecloud.api.node.StreamlineNode
import net.streamlinecloud.backend.repository.NodeRepository
import net.streamlinecloud.backend.service.ActiveSessionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.SecureRandom
import java.util.Base64
import java.util.Optional
import java.util.UUID

/*
 * Responsible for the current session (and its node if present)
 */

@RestController
@RequestMapping("/session")
class SessionController(
    val nodeRepository: NodeRepository,
    val sessionService: ActiveSessionService
) {

    @PostMapping("/validate/{uuid}")
    fun validate(
        @RequestBody key: String,
        @PathVariable uuid: String
    ): ResponseEntity<StreamlineNode> {

        val node: Optional<StreamlineNode> = nodeRepository.findByUuid(uuid)

        if (!node.isPresent)
            return ResponseEntity.notFound().build()


        if (node.get().key != key)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(
            StreamlineNode(
                node.get().uuid,
                node.get().isMain,
                null
            )
        );

    }

    @GetMapping("/setup")
    fun register(): ResponseEntity<StreamlineNode> {

        if (!nodeRepository.findAll().toList().isEmpty())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.ok(
            nodeRepository.save(
                StreamlineNode(
                    UUID.randomUUID().toString(),
                    true,
                    "node_" + generateApiKey(48)
                )
            )
        )

    }

    @GetMapping("/info")
    fun info(authentication: Authentication): ResponseEntity<StreamlineNode>? {
        val node: StreamlineNode = authentication.principal as StreamlineNode

        if (sessionService.activeSessions.containsKey(nodeRepository.findByUuid(node.uuid).get().key))
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/templates")
    fun templates(
        authentication: Authentication,
        @RequestBody templates: List<String>
    ): ResponseEntity<Void> {
        val node: StreamlineNode = authentication.principal as StreamlineNode
        sessionService.templates.set(nodeRepository.findByUuid(node.uuid).get().key, templates)
        return ResponseEntity.ok().build();
    }

    @GetMapping("/secret")
    fun secret(): String = "Secret lol"

    fun generateApiKey(length: Int = 32): String {
        val random = SecureRandom()
        val bytes = ByteArray(length)
        random.nextBytes(bytes)

        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(bytes)
    }
}