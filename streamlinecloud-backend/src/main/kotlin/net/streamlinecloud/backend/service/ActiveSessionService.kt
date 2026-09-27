package net.streamlinecloud.backend.service

import net.streamlinecloud.api.group.StreamlineGroup
import net.streamlinecloud.api.node.StreamlineNode
import net.streamlinecloud.api.session.ActiveBackendSession
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class ActiveSessionService {

    val activeSessions = ArrayList<ActiveBackendSession>()

    /*
     * All groups that failed to start because there was no suitable node.
     */
    val groupsWithoutSuitableNode = mutableSetOf<String>()

    fun getSession(key: String): ActiveBackendSession? {
        return activeSessions.find { session -> session.childId == key }
    }

    fun setTemplates(key: String, template: List<String>) {
        getSession(key)?.templates = template;
    }

}