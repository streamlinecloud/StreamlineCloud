package net.streamlinecloud.mc.common.core.manager;

import com.google.gson.Gson;
import net.streamlinecloud.api.group.StreamlineGroup;
import net.streamlinecloud.mc.SpigotSCP;
import net.streamlinecloud.mc.common.utils.Functions;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AbstractGroupManager implements GroupManagerImpl {

    @Getter
    private static AbstractGroupManager instance;

    @Getter
    List<StreamlineGroup> groups = new ArrayList<>();

    public AbstractGroupManager() {
        instance = this;
        startTask();
    }

    public void updateGroups() {
        List<String> servers = new ArrayList<>();
        servers = new Gson().fromJson(Functions.get("groups"), servers.getClass());

        for (String name : servers) {
            if (!groupExists(name)) {
                try {
                    StreamlineGroup streamlineGroup = new Gson().fromJson(Functions.get("groups/" + name), StreamlineGroup.class);
                    groups.add(streamlineGroup);
                } catch (Exception e) {
                    SpigotSCP.getInstance().getLogger().info("error!" + e.getMessage());
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public void onUpdate(StreamlineGroup group) {
    }

    private void startTask() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable runnable = () -> {
            updateGroups();
        };

        scheduler.scheduleAtFixedRate(runnable, 0, 20, TimeUnit.SECONDS);
    }

    public boolean groupExists(String name) {
        for (StreamlineGroup server : groups) {
            if (server.getName().toString().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public StreamlineGroup getGroup(String name) {
        for (StreamlineGroup g : groups) if (g.getName().equals(name)) return g;
        return null;
    }

}
