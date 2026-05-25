package net.streamlinecloud.main.core.group;

import com.google.gson.Gson;
import lombok.Getter;
import net.streamlinecloud.api.group.StreamlineGroup;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.utils.Cache;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Scans for local installed templates, saves them, and sends the information to the backend.
 * When all templates a group requires are reported, this node can receive start server jobs for the group.
 */
@Getter
public class GroupTemplatesManager {

    String templatesPath;
    List<String> localTemplates;

    public GroupTemplatesManager() {
        templatesPath = Cache.i().getHomeFile() + "/templates";
        localTemplates = new ArrayList<>();
    }

    /**
     * Scans for local templates and uploads the information to the backend.
     */
    public void update() {
        localTemplates.clear();

        for (StreamlineGroup group : StreamlineCloud.getGroupManager().getGroups()) {
            boolean addAll = true;
            for (String template : group.getTemplates())
                if (!new File(templatesPath + "/" + template).exists()) addAll = false;

            if (addAll) localTemplates.addAll(group.getTemplates());
        }

        upload();
    }

    private void upload() {
        StreamlineCloud.getHttpClient().runBlockingPut(
                "/session/templates",
                new Gson().toJson(localTemplates.toArray())
        );
    }

    /**
     * @return A list of all groups that are installed on this node based on the scanned templates.
     */
    public List<StreamlineGroup> getLocalGroups() {
        List<StreamlineGroup> groups = new ArrayList<>(StreamlineCloud.getGroupManager().getGroups());

        for (StreamlineGroup group : groups) {
            for (String template : group.getTemplates()) {
                if (localTemplates.contains(template)) groups.remove(group);
            }
        }

        return groups;
    }

}
