package net.streamlinecloud.backend.service

import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class ActiveSessionService {

    //nodeUuid -> sessionId
    val activeSessions = ConcurrentHashMap<String, String>()

    //nodeUuid -> templates
    val templates = ConcurrentHashMap<String, List<String>>()

    fun getSessionIdByKey(key: String): String? {
        return activeSessions[key]
    }

}