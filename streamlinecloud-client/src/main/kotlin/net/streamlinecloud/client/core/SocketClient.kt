package net.streamlinecloud.net.streamlinecloud.client.core

import com.google.gson.Gson
import net.streamlinecloud.api.group.StreamlineGroup
import net.streamlinecloud.api.socket.SocketResponse
import net.streamlinecloud.net.streamlinecloud.client.adapter.SocketAdapter
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.subscribeText
import org.hildan.krossbow.websocket.ktor.KtorWebSocketClient
import kotlin.streams.toList

class SocketClient(
    val url: String,
    private val onSuccess: () -> Unit,
    private val onError: (Throwable) -> Unit,
    private val onDisconnect: () -> Unit
) {

    var client: StompClient? = null
    var session: StompSession? = null

    val adapters: MutableList<SocketAdapter> = mutableListOf()
    private val subscribedTopics: MutableList<String> = mutableListOf()

    suspend fun connect() {
        client = StompClient(KtorWebSocketClient())
        session = client?.connect("$url/socket")

        onSuccess()
    }

    private suspend fun subscribe(topic: String) {
        subscribedTopics.add(topic)

        session?.subscribeText(topic)?.collect { msg ->
            println(msg.toString())
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

}