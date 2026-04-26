package net.streamlinecloud.backend.config

import net.streamlinecloud.api.node.StreamlineNode
import net.streamlinecloud.backend.repository.NodeRepository
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
    private val nodeRepository: NodeRepository
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

                if (accessor?.command == StompCommand.CONNECT) {
                    val authHeader = accessor.getFirstNativeHeader("Authorization")

                    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        throw MessageDeliveryException("Missing or invalid Authorization header")
                    }

                    val token = authHeader.removePrefix("Bearer ").trim()
                    val node = nodeRepository.findByKey(token) ?: throw MessageDeliveryException("Invalid API key")

                    accessor.user = UsernamePasswordAuthenticationToken(
                        node, null,
                        listOf(SimpleGrantedAuthority("ROLE_API_USER"))
                    )
                }

                return message
            }
        })
    }

}