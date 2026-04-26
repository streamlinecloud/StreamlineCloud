package net.streamlinecloud.client.manager

import com.google.gson.Gson
import io.ktor.client.statement.bodyAsText
import net.streamlinecloud.api.group.StreamlineGroup
import net.streamlinecloud.api.socket.SocketResponse
import net.streamlinecloud.client.adapter.SocketAdapter
import net.streamlinecloud.client.core.StreamlineApiClient

/**
 * An api class for managing all [StreamlineGroup]'s existing in the connected cluster.
 * Changes are updatet in real time due to the socket connection.
 *
 * **Important:** This is not the place where the data is saved. This class only mirrors the data and sends the changes to the backend.
 */
class GroupManager(
    val apiClient: StreamlineApiClient
) : SocketAdapter {

    override val topic: String = "/topic/groups"

    private val groups: MutableList<StreamlineGroup> = mutableListOf()

    suspend fun init() {
        Gson().fromJson(apiClient.httpClient.get("/groups").bodyAsText(), List::class.java).forEach { group ->
            this.groups.add(Gson().fromJson(group.toString(), StreamlineGroup::class.java))
        }
    }

    /**
     * An internal function used to r receive new data
     */
    override fun receive(response: SocketResponse) {
        val new: StreamlineGroup = Gson().fromJson(response.content.toString(), StreamlineGroup::class.java)

        groups.removeIf { group -> group.name == new.name }
        groups.add(new)
    }

    /**
     * This functions saves and activates a new group.
     * @param group A new CloudGroup instance
     * @return True if created successfully, false otherwise
     */
    suspend fun create(group: StreamlineGroup): Boolean {
        if (groupExists(group.name)) return false
        apiClient.httpClient.put("/groups", Gson().toJson(group))
        return true
    }

    /**
     * Updates a group based on its name
     *
     * @param group The group with the old name and the updated data
     * @return True if updated successfully, false otherwise
     */
    suspend fun update(group: StreamlineGroup): Boolean {
        if (!groupExists(group.name)) return false
        apiClient.httpClient.put("/groups", Gson().toJson(group))
        return true
    }

    fun delete(group: StreamlineGroup) {
        TODO("NOT YET IMPLEMENTED")
    }

    /**
     * Finds a group by its name.
     *
     * @return The [StreamlineGroup] with the given name, or `null` if not found.
     */
    fun getGroup(name: String): StreamlineGroup? {
        return groups.find { group -> group.name == name }
    }

    /**
     * Checks if a group with a specific name exists
     *
     * @return True if a [StreamlineGroup] with the given name exists, otherwise false
     */
    fun groupExists(name: String): Boolean {
        return groups.stream().anyMatch { existingGroup -> existingGroup.name == name }
    }
}