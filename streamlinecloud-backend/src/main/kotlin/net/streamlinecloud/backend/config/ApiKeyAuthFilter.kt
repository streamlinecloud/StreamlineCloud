package net.streamlinecloud.backend.config

import io.swagger.v3.core.util.Json
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.streamlinecloud.api.node.StreamlineNode
import net.streamlinecloud.backend.service.NodeService
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter

@Component
class ApiKeyAuthFilter(
    val nodeService: NodeService
) : OncePerRequestFilter() {

    val publicPaths = listOf(
        "/session/setup",
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

        val uuid: String? = nodeService.getUuidByKey(token)
        if (uuid == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
            return
        }

        val node: StreamlineNode? = nodeService.getByUuid(uuid)
        if (node == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
            return
        }

        val auth = UsernamePasswordAuthenticationToken(
            node, null,
            listOf(SimpleGrantedAuthority("ROLE_API_USER"))
        )
        SecurityContextHolder.getContext().authentication = auth

        filterChain.doFilter(request, response)
    }
}