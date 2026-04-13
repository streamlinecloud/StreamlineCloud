package net.streamlinecloud.client.manager

import com.google.gson.Gson
import io.ktor.client.statement.bodyAsText
import net.streamlinecloud.api.group.StreamlineGroup
import net.streamlinecloud.api.socket.SocketResponse
import net.streamlinecloud.client.adapter.SocketAdapter
import net.streamlinecloud.client.core.StreamlineApiClient
import java.util.Optional

class GroupManager(
    val apiClient: StreamlineApiClient
) : SocketAdapter {

    override val topic: String = "/topic/groups"

    val groups: MutableList<StreamlineGroup> = mutableListOf()

    suspend fun init() {
        Gson().fromJson(apiClient.httpClient.get("/groups").bodyAsText(), List::class.java).forEach { group ->
            this.groups.add(Gson().fromJson(group.toString(), StreamlineGroup::class.java))
        }
    }

    override fun receive(response: SocketResponse) {
        val new: StreamlineGroup = Gson().fromJson(response.content.toString(), StreamlineGroup::class.java)

        groups.removeIf { group -> group.name == new.name }
        groups.add(new)
    }
}