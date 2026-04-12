package net.streamlinecloud.backend.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.streamlinecloud.api.node.StreamlineNode
import net.streamlinecloud.backend.repository.NodeRepository
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter

@Component
class ApiKeyAuthFilter(
    private val nodeRepository: NodeRepository
) : OncePerRequestFilter() {

    val publicPaths = listOf(
        "/sessions/validate",
        "/sessions/setup",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/public/**",
        "/socket/**"
    )

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        return publicPaths.any { AntPathMatcher().match(it, path) }
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader(HttpHeaders.AUTHORIZATION)

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
            return
        }

        val token = authHeader.removePrefix("Bearer ").trim()

        val node: StreamlineNode? = nodeRepository.findByKey(token)
        if (node == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
            return
        }

        node.key = null

        val auth = UsernamePasswordAuthenticationToken(
            node, null,
            listOf(SimpleGrantedAuthority("ROLE_API_USER"))
        )
        SecurityContextHolder.getContext().authentication = auth

        filterChain.doFilter(request, response)
    }
}