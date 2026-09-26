package net.streamlinecloud.backend.service

import net.streamlinecloud.api.session.ActiveBackendSession
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class ActiveSessionService {

    val activeSessions = ArrayList<ActiveBackendSession>()

    //nodeUuid -> sessionId
    val oldActiveSessions = ConcurrentHashMap<String, String>()

    //nodeUuid -> templates
    val templates = ConcurrentHashMap<String, List<String>>()

    //sessionId -> onlineServers
    val servers = ConcurrentHashMap<String, List<String>>()

    fun getSession(key: String): ActiveBackendSession? {
        return activeSessions.find { session -> session.childId == key }
    }

    fun setTemplates(key: String, template: List<String>) {
        getSession(key)?.templates = template;
    }

}