package net.streamlinecloud.client.core

import com.google.gson.Gson
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.runBlocking
import net.streamlinecloud.api.node.StreamlineNode
import net.streamlinecloud.api.terminal.StreamlineLogger
import net.streamlinecloud.client.manager.GroupManager

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

    val socketClient: StreamlineSocketClient = StreamlineSocketClient(
        socketUrl,
        onSuccess = {
            logger.info("Backend connection established")
        },
        onError = {
            logger.info("Backend connection error: ${it.message}")
        },
        onDisconnect = {
            logger.warning("Disconnected from backend")
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
                connected = true

            } catch (e: Exception) {
                onError("Connection lost: ${e.message}")
            }
        }
    }

    private suspend fun connectSocket() {
        groupManager.init()

        socketClient.connect(key)
        socketClient.inject(groupManager)
    }

    suspend fun disconnect() {
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