package net.streamlinecloud.client.manager

import net.streamlinecloud.api.socket.SocketRequest
import net.streamlinecloud.api.socket.SocketResponse
import net.streamlinecloud.client.adapter.SocketAdapter
import net.streamlinecloud.client.core.StreamlineApiClient

class HeartbeatManager(
    val apiClient: StreamlineApiClient
): SocketAdapter {

    override val topic = "/topic/heartbeat"

    override fun receive(response: SocketResponse) {
        TODO("Not yet implemented")
    }

    /**
     * Send a heartbeat to the backend. This is used to keep the connection alive.
     */
    suspend fun updateHeartbeat() {
        send(SocketRequest(null))
    }

    /**
     * Utility function to send heartbeat, do not use this directly, use [updateHeartbeat] instead.
     */
    override suspend fun send(request: SocketRequest?) {
        apiClient.socketClient.send("/heartbeat", request ?: SocketRequest(null))
    }
}