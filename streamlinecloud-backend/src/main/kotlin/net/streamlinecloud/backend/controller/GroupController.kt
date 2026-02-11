package net.streamlinecloud.backend.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class GroupController {

    @GetMapping("/groups")
    fun getGroups(): String {
        return "groups"
    }

}