package net.streamlinecloud.backend.controller

import net.streamlinecloud.api.node.StreamlineNode
import net.streamlinecloud.backend.entity.Credential
import net.streamlinecloud.backend.repository.NodeRepository
import net.streamlinecloud.backend.service.ActiveSessionService
import net.streamlinecloud.backend.service.NodeService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/*
 * Responsible for the current session (and its node if present)
 */

@RestController
@RequestMapping("/session")
class SessionController(
    val nodeRepository: NodeRepository,
    val nodeService: NodeService,
    val sessionService: ActiveSessionService
) {

    @GetMapping("/setup")
    fun register(): ResponseEntity<Credential> {

        if (!nodeRepository.findAll().toList().isEmpty())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.ok(
            nodeService.register("Main Node", isWorker = true, isAdmin = true)
        )

    }

    @GetMapping("/info")
    fun info(authentication: Authentication): ResponseEntity<StreamlineNode>? {
        val node: StreamlineNode = authentication.principal as StreamlineNode

        if (sessionService.activeSessions.containsKey(node.uuid))
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        return ResponseEntity.status(HttpStatus.OK).body(node);
    }

    @PutMapping("/templates")
    fun templates(
        authentication: Authentication,
        @RequestBody templates: List<String>
    ): ResponseEntity<Void> {
        val node: StreamlineNode = authentication.principal as StreamlineNode
        sessionService.templates.set(node.uuid, templates)
        return ResponseEntity.ok().build();
    }

}