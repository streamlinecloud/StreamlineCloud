package net.streamlinecloud.net.streamlinecloud.client.core

import com.google.gson.Gson
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import net.streamlinecloud.api.node.StreamlineNode
import net.streamlinecloud.net.streamlinecloud.client.manager.GroupManager

class StreamlineApiClient(
    val url: String,
    val socketUrl: String,
    val key: String,
    private val onError: (String) -> Unit,
) {

    var connected = false
    var node: StreamlineNode? = null

    val groupManager: GroupManager = GroupManager(this)

    val socketClient: StreamlineSocketClient = StreamlineSocketClient(
        socketUrl,
        onSuccess = {
            println("Socket connected")
        },
        onError = {
            println("Error: $it")
        },
        onDisconnect = {
            println("Disconnected!")
        }
    )

    val httpClient: StreamlineHttpClient = StreamlineHttpClient(url, key)

    suspend fun connect() {
        if (connected) return

        try {
            val res: HttpResponse = httpClient.get("/session/info")
            if (res.status.value != 200) {
                onError("Initial handshake failed with status code " + res.status)
                return
            }

            node = Gson().fromJson(res.bodyAsText(), StreamlineNode::class.java)
            println("Connected with node ${node?.uuid} (main=${node?.isMain})")

            connectSocket()
            connected = true

        } catch (e: Exception) {
            onError("Cannot connect to the backend: ${e.message}")
        }
    }

    private suspend fun connectSocket() {
        groupManager.init()

        socketClient.connect(key)
        socketClient.inject(groupManager)
    }

}