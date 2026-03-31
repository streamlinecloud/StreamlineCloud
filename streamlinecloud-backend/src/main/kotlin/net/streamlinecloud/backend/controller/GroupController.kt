package net.streamlinecloud.backend.controller

import net.streamlinecloud.api.group.StreamlineGroup
import net.streamlinecloud.backend.repository.GroupRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/groups")
class GroupController (private val groupRepository: GroupRepository) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun list() = groupRepository.findAll().toList()

    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody group: StreamlineGroup): StreamlineGroup = groupRepository.save(group)

}