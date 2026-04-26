package net.streamlinecloud.backend.service

import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class ActiveSessionService {

    //key -> sessionId
    val activeSessions = ConcurrentHashMap<String, String>()

}