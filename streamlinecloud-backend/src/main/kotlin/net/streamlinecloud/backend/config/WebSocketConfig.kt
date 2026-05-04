package net.streamlinecloud.backend.config

import net.streamlinecloud.api.node.StreamlineNode
import net.streamlinecloud.backend.service.ActiveSessionService
import net.streamlinecloud.backend.service.NodeService
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.MessageDeliveryException
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer


@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig(
    val nodeService: NodeService,
    val sessionService: ActiveSessionService
) : WebSocketMessageBrokerConfigurer {

    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.enableSimpleBroker("/topic")
        config.setApplicationDestinationPrefixes("/app")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/socket")
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(object : ChannelInterceptor {
            override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {
                val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)

                when (accessor?.command) {
                    StompCommand.CONNECT -> {
                        val authHeader = accessor.getFirstNativeHeader("Authorization")
                        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                            throw MessageDeliveryException("Missing or invalid Authorization header")
                        }

                        val token = authHeader.removePrefix("Bearer ").trim()

                        val uuid: String = nodeService.getUuidByKey(token)
                            ?: throw MessageDeliveryException("Invalid API key")

                        val node: StreamlineNode = nodeService.getByUuid(uuid)
                            ?: throw MessageDeliveryException("Key valid, no node found")

                        if (sessionService.activeSessions.containsKey(token)) {
                            throw MessageDeliveryException("This node is already connected")
                        }

                        sessionService.activeSessions[token] = accessor.sessionId ?: ""
                        accessor.user = UsernamePasswordAuthenticationToken(
                            node, null,
                            listOf(SimpleGrantedAuthority("ROLE_API_USER"))
                        )
                    }

                    StompCommand.DISCONNECT -> {
                        val sessionId = accessor.sessionId
                        sessionService.activeSessions.entries.removeIf { it.value == sessionId }
                    }

                    else -> {}
                }

                return message
            }
        })
    }
}