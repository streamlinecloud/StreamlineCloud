package net.streamlinecloud.backend.controller

import net.streamlinecloud.backend.service.ActiveSessionService
import net.streamlinecloud.backend.service.NodeService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/*
 * Responsible for all nodes and the management of them
 */

@RestController()
@RequestMapping("/nodes")
class NodeController(
    val nodeService: NodeService
) {

    @GetMapping
    fun getAll() = nodeService.getAllWithOnlineInf()

}