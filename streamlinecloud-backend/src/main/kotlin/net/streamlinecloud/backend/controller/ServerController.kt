package net.streamlinecloud.backend.controller

import net.streamlinecloud.api.server.StreamlineServer
import net.streamlinecloud.backend.service.ServerService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/*
 * Responsible for all servers - online, starting or restarting.
 * Information about online servers is always cached and never stored in the database.
 */

@RestController()
@RequestMapping("/servers")
class ServerController(
    private val serverService: ServerService,
) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getAll() = serverService.runningServers

    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun update(@RequestBody server: StreamlineServer): StreamlineServer = serverService.update(server)


}