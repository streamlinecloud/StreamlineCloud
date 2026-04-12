package net.streamlinecloud.net.streamlinecloud.client

import net.streamlinecloud.net.streamlinecloud.client.adapter.GroupAdapter
import net.streamlinecloud.net.streamlinecloud.client.core.SocketClient

suspend fun main() {

    val client = SocketClient("ws://localhost:8080", {
        println("Socket client connected!")
    }, { throwable ->
        println("Socket error occurred: ${throwable.message}")
    }, {
        println("Socket client disconnected!")
    })

    client.connect("node_i8UPpwKxXVy_B4KSFK1cl4ZI_ZRtXfqOQBPW3nCU3xwWvI0uCJnHPExZ3XQ7nDwV")

    client.inject(GroupAdapter())

}