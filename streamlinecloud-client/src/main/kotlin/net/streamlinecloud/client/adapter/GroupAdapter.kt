package net.streamlinecloud.net.streamlinecloud.client.adapter

import com.google.gson.Gson
import net.streamlinecloud.api.group.StreamlineGroup
import net.streamlinecloud.api.socket.SocketResponse

class GroupAdapter : SocketAdapter {

    override val topic = "/topic/groups"

    override fun receive(response: SocketResponse) {
        val group: StreamlineGroup = Gson().fromJson(response.content.toString(), StreamlineGroup::class.java)
        println("Group received: ${group.name}")
    }
}