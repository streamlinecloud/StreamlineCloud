package net.streamlinecloud.net.streamlinecloud.client.core

import com.google.gson.Gson
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.WebSockets
import net.streamlinecloud.api.socket.SocketResponse
import net.streamlinecloud.net.streamlinecloud.client.adapter.SocketAdapter
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.subscribeText
import org.hildan.krossbow.websocket.ktor.KtorWebSocketClient
import java.net.http.HttpClient

class StreamlineSocketClient(
    val url: String,
    private val onSuccess: () -> Unit,
    private val onError: (Throwable) -> Unit,
    private val onDisconnect: () -> Unit
) {

    var client: StompClient? = null
    var session: StompSession? = null

    val adapters: MutableList<SocketAdapter> = mutableListOf()
    private val subscribedTopics: MutableList<String> = mutableListOf()

    suspend fun connect(key: String) {

        try {
            client = StompClient(KtorWebSocketClient(
            ))
            session = client?.connect(
                url = "$url/socket",
                customStompConnectHeaders = mapOf("Authorization" to "Bearer $key")
            )
        } catch (e: Exception) {
            onError(e)
            return
        }

        onSuccess()
    }

    private suspend fun subscribe(topic: String) {
        subscribedTopics.add(topic)

        session?.subscribeText(topic)?.collect { msg ->
            run {
                adapters.stream().filter { it.topic == topic }.toList().forEach { adapter ->
                    adapter.receive(Gson().fromJson(msg, SocketResponse::class.java))
                }
            }
        }
    }

    suspend fun inject(adapter: SocketAdapter) {
        adapters.add(adapter)
        if (!subscribedTopics.contains(adapter.topic)) subscribe(adapter.topic)
    }

    suspend fun disconnect() {
        session?.disconnect()
        onDisconnect()
    }

}