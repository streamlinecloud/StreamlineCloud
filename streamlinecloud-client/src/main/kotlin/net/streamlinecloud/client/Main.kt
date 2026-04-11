package net.streamlinecloud.net.streamlinecloud.client

import net.streamlinecloud.net.streamlinecloud.client.adapter.GroupAdapter
import net.streamlinecloud.net.streamlinecloud.client.core.SocketClient

suspend fun main() {

    val client = SocketClient("ws://localhost:8080", {
        println("Socket client connected!")
    }, {
        println("Socket error occurred!")
    }, {
        println("Socket client disconnected!")
    })

    client.connect()

    client.inject(GroupAdapter())

}