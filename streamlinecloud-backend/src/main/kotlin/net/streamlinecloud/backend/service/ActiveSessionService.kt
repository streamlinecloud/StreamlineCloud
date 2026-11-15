package net.streamlinecloud.backend.service

import net.streamlinecloud.api.session.ActiveBackendSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ActiveSessionService {

    val activeSessions = ArrayList<ActiveBackendSession>()
    val logger: Logger = LoggerFactory.getLogger(ActiveSessionService::class.java)

    /*
     * All groups that failed to start because there was no suitable node.
     */
    val groupsWithoutSuitableNode = mutableSetOf<String>()

    fun getSession(key: String): ActiveBackendSession? {
        return activeSessions.find { session -> session.childId == key }
    }

    /**
     * Gets the session with the given session ID.
     * @param sessionId The session ID of the session.
     * @return The session with the given session ID, or null if no such session exists
     */
    fun getSessionBySessionId(sessionId: String): ActiveBackendSession? {
        return activeSessions.find { session -> session.sessionId == sessionId }
    }

    fun setTemplates(key: String, template: List<String>) {
        getSession(key)?.templates = template;
    }

    /**
     * Gets the last heartbeat timestamp for the session with the given key.
     * @param key The key of the session.
     * @return The last heartbeat timestamp, or null if the session does not exist.
     */
    fun getHeartbeat(key: String): Long? {
        return getSessionBySessionId(key)?.lastHeartbeat
    }

    /**
     * Updates the last heartbeat timestamp for the session with the given key to the current system time.
     * @param key The key of the session.
     */
    fun updateHeartbeat(key: String) {
        getSessionBySessionId(key)?.lastHeartbeat = System.currentTimeMillis()
    }

    @Scheduled(fixedRate = 10_000)
    private fun clearDeadSessions() {
        val currentTime = System.currentTimeMillis()
        val deadSessions = activeSessions.filter { session ->
            currentTime - session.lastHeartbeat > 10_000
        }
        deadSessions.forEach { session ->
            activeSessions.remove(session)
            logger.debug("Removed dead session: ${session.sessionId} with last heartbeat: ${session.lastHeartbeat}")
        }
    }

}