package net.streamlinecloud.net.streamlinecloud.client

import net.streamlinecloud.net.streamlinecloud.client.core.StreamlineApiClient

suspend fun main() {

    StreamlineApiClient(
        "http://localhost:8080",
        "ws://localhost:8080",
        "node_i8UPpwKxXVy_B4KSFK1cl4ZI_ZRtXfqOQBPW3nCU3xwWvI0uCJnHPExZ3XQ7nDwV",
        {msg ->
            println(msg)
        }
    ).connect()

}