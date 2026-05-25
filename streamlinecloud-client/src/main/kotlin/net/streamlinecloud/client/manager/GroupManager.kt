package net.streamlinecloud.client.manager

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.runBlocking
import net.streamlinecloud.api.group.StreamlineGroup
import net.streamlinecloud.api.socket.SocketResponse
import net.streamlinecloud.api.socket.SocketResponseType
import net.streamlinecloud.client.adapter.SocketAdapter
import net.streamlinecloud.client.core.StreamlineApiClient

/**
 * An api class for managing all [StreamlineGroup]'s existing in the connected cluster.
 * Changes are updated in real time due to the socket connection.
 *
 * **Important:** This is not the place where the data is saved. This class only mirrors the data and sends the changes to the backend.
 */
class GroupManager(
    val apiClient: StreamlineApiClient
) : SocketAdapter {

    override val topic: String = "/topic/groups"

    private val groups: MutableList<StreamlineGroup> = mutableListOf()

    suspend fun init() {
        val groupListType = object : TypeToken<List<StreamlineGroup>>() {}.type
        val groups: List<StreamlineGroup> = Gson().fromJson(apiClient.httpClient.get("/groups").bodyAsText(), groupListType)
        this.groups.addAll(groups)
        if (groups.isEmpty()) apiClient.logger.info("Currently, there aren't any groups. Go an and create one: groups create");
    }

    /**
     * @return All [StreamlineGroup]'s existing in the current cluster
     */
    fun getGroups(): List<StreamlineGroup> {
        return groups.toList()
    }

    /**
     * An internal function used to receive new data
     */
    override fun receive(response: SocketResponse) {
        if (!response.sender.equals("GroupService")) return

        val gson = Gson()
        val contentJson: String = gson.toJson(response.content)
        val new: StreamlineGroup = gson.fromJson(contentJson, object : TypeToken<StreamlineGroup>() {}.type)
        if (response.responseType == SocketResponseType.UPDATE) {
            groups.removeIf { group -> group.name == new.name }
            groups.add(new)

        } else if (response.responseType == SocketResponseType.DELETE) {
            groups.removeIf { group -> group.name == new.name }

        }

    }

    /**
     * This functions saves and activates a new group.
     * @param group A new CloudGroup instance
     * @return True if created successfully, false otherwise
     */
    fun create(group: StreamlineGroup): Boolean {
        return runBlocking {
            if (groupExists(group.name)) return@runBlocking false
            apiClient.httpClient.put("/groups", Gson().toJson(group))
            true
        }
    }

    /**
     * Updates a group based on its name
     *
     * @param group The group with the old name and the updated data
     * @return True if updated successfully, false otherwise
     */
    fun update(group: StreamlineGroup): Boolean {
        return runBlocking {
            if (!groupExists(group.name)) return@runBlocking false
            apiClient.httpClient.put("/groups", Gson().toJson(group))
            true
        }
    }

    fun delete(group: StreamlineGroup) {
        return runBlocking {
            apiClient.httpClient.delete("/groups", Gson().toJson(group))
        }
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