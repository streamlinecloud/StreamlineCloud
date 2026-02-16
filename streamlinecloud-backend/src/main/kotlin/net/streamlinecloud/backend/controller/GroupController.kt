package net.streamlinecloud.backend.controller

import net.streamlinecloud.api.group.StreamlineGroup
import net.streamlinecloud.backend.repository.GroupRepository
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody group: StreamlineGroup): StreamlineGroup = groupRepository.save(group)

}