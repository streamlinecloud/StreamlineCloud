package net.streamlinecloud.backend.socket

import net.streamlinecloud.api.socket.SocketResponse
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller

@Controller
class GroupSocket(
    private val messagingTemplate: SimpMessagingTemplate
) {

    fun sendToAll(response: SocketResponse) {
        messagingTemplate.convertAndSend("/topic/groups", response)
    }

}