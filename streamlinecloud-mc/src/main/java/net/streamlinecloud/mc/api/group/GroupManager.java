package net.streamlinecloud.mc.api.group;

import com.google.gson.Gson;
import net.streamlinecloud.api.group.StreamlineGroup;
import net.streamlinecloud.mc.SpigotSCP;
import net.streamlinecloud.mc.utils.Functions;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GroupManager {

    @Getter
    private static GroupManager instance;

    @Getter
    List<StreamlineGroup> groups = new ArrayList<>();

    public GroupManager() {
        instance = this;
        startTask();
    }

    private void updateGroups() {
        List<String> servers = new ArrayList<>();
        servers = new Gson().fromJson(Functions.get("get/allgroups"), servers.getClass());

        for (String name : servers) {
            if (!groupExists(name)) {
                try {
                    StreamlineGroup streamlineGroup = new Gson().fromJson(Functions.get("get/groupdata/" + name), StreamlineGroup.class);
                    groups.add(streamlineGroup);
                } catch (Exception e) {
                    SpigotSCP.getInstance().getLogger().info("error!" + e.getMessage());
                    e.printStackTrace();
                }

            }
        }
    }
    private void startTask() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable runnable = () -> {
            updateGroups();
        };

        scheduler.scheduleAtFixedRate(runnable, 0, 20, TimeUnit.SECONDS);
    }

    //GroupExists
    private boolean groupExists(String name) {
        for (StreamlineGroup server : groups) {
            if (server.getName().toString().equals(name)) {
                return true;
            }
        }
        return false;
    }

    //GetGroup

    /**
     * Get a group by name
     * @param name String name of the group
     * @return StreamlineGroup or null
     */
    public StreamlineGroup getGroup(String name) {
        for (StreamlineGroup g : groups) if (g.getName().equals(name)) return g;
        return null;
    }

}
