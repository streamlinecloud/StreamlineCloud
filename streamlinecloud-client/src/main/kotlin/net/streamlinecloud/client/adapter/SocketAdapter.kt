package net.streamlinecloud.net.streamlinecloud.client.adapter

import net.streamlinecloud.api.socket.SocketResponse

interface SocketAdapter {

    val topic: String

    fun receive(response: SocketResponse)

}