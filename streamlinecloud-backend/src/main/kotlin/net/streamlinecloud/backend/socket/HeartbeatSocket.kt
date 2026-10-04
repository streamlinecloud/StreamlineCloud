package net.streamlinecloud.backend.socket

import net.streamlinecloud.api.socket.SocketRequest
import net.streamlinecloud.backend.service.ActiveSessionService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.stereotype.Controller

@Controller
class HeartbeatSocket(
    val sessionService: ActiveSessionService
) {

    val logger: Logger = LoggerFactory.getLogger(HeartbeatSocket::class.java)

    @MessageMapping("/heartbeat")
    fun handleHeartbeat(
        @Payload request: SocketRequest,
        headerAccessor: SimpMessageHeaderAccessor
    ) {
        val sessionId = headerAccessor.sessionId

        if (sessionId != null) {
            sessionService.updateHeartbeat(sessionId)
            logger.debug("Received heartbeat from $sessionId new heartbeat timestamp: ${sessionService.getHeartbeat(sessionId)}")
        } else {
            logger.warn("Received heartbeat with null sessionId")
        }

    }

}