package net.streamlinecloud.client.manager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import net.streamlinecloud.api.socket.SocketRequest
import net.streamlinecloud.api.socket.SocketResponse
import net.streamlinecloud.client.adapter.SocketAdapter
import net.streamlinecloud.client.core.StreamlineApiClient
import kotlin.time.Duration.Companion.milliseconds

class HeartbeatManager(
    val apiClient: StreamlineApiClient
): SocketAdapter {

    private var heartbeatJob: Job? = null
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

    fun startHeartbeatJob() {

        apiClient.logger.debug("Starting heartbeat job")

        heartbeatJob?.cancel()
        heartbeatJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                delay(2_000.milliseconds)
                if (apiClient.connected) updateHeartbeat()
            }
        }
    }

    fun stopHeartbeatJob() {
        apiClient.logger.debug("Stopping heartbeat job")
        heartbeatJob?.cancel()
        heartbeatJob = null
    }

}