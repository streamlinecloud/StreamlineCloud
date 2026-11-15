package net.streamlinecloud.client.core

import com.google.gson.Gson
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.streamlinecloud.api.node.StreamlineNode
import net.streamlinecloud.api.terminal.StreamlineLogger
import net.streamlinecloud.client.manager.GroupManager
import net.streamlinecloud.client.manager.HeartbeatManager
import kotlin.time.Duration.Companion.milliseconds

class StreamlineApiClient(
    val url: String,
    val socketUrl: String,
    val key: String,
    val logger: StreamlineLogger,
    private val onError: (String) -> Unit,
) {

    var connected = false
    var node: StreamlineNode? = null

    val groupManager: GroupManager = GroupManager(this)
    val heartbeatManager: HeartbeatManager = HeartbeatManager(this)

    val socketClient: StreamlineSocketClient = StreamlineSocketClient(
        socketUrl,
        onSuccess = {
            connected = true
            logger.info("Backend connection established")
            heartbeatManager.startHeartbeatJob()
        },
        onError = {
            logger.info("Backend connection error: ${it.message}")
        },
        onDisconnect = {
            logger.warning("Disconnected from backend")
            heartbeatManager.stopHeartbeatJob()
        }
    )

    val httpClient: StreamlineHttpClient = StreamlineHttpClient(url, key)

    fun connect() {
        runBlocking {
            if (connected) return@runBlocking "done"

            try {
                val res: HttpResponse = httpClient.get("/session/info")
                if (res.status.value != 200) {
                    onError("Initial handshake failed with status code " + res.status)
                    return@runBlocking "done"
                }

                node = Gson().fromJson(res.bodyAsText(), StreamlineNode::class.java)
                logger.debug("Connected with node ${node?.uuid} (admin=${node?.isAdmin}) (worker=${node?.isWorker})")

                connectSocket()

            } catch (e: Exception) {
                onError("Connection lost: ${e.message}")
            }
        }
    }

    private suspend fun connectSocket() {
        groupManager.init()

        socketClient.connect(key)
        socketClient.inject(groupManager)
        socketClient.inject(heartbeatManager)
    }

    suspend fun disconnect() {
        heartbeatManager.stopHeartbeatJob()
        socketClient.disconnect()
        connected = false
    }

    private suspend fun reconnect() {
        println("reconnecting...")
        connected = false
        socketClient.disconnect()

        connect()
    }

}