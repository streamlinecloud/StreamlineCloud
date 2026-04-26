package net.streamlinecloud.backend.controller

import net.streamlinecloud.api.group.StreamlineGroup
import net.streamlinecloud.api.socket.SocketRequest
import net.streamlinecloud.api.socket.SocketResponse
import net.streamlinecloud.backend.repository.GroupRepository
import net.streamlinecloud.backend.service.GroupService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/groups")
class GroupController (
    private val groupService: GroupService
) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun list() = groupService.findAll()

    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody group: StreamlineGroup): StreamlineGroup = groupService.update(group)

    @DeleteMapping
    fun delete(@RequestBody group: StreamlineGroup): ResponseEntity<Void> {
        groupService.delete(group)
        return ResponseEntity(HttpStatus.OK)
    }

    /*@MessageMapping("")
    @SendTo("/groups/subscribe")
    fun greeting(message: SocketRequest): SocketResponse? {
        Thread.sleep(1000)
        return SocketResponse("Hello World from Spring - Req: " + message.content, "backend")
    }*/

}