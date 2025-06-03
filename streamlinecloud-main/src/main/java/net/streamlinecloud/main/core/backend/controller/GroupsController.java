package net.streamlinecloud.main.core.backend.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.http.Context;
import net.streamlinecloud.api.group.StreamlineGroup;
import net.streamlinecloud.api.server.StreamlineServer;
import net.streamlinecloud.api.server.StreamlineServerSerializer;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.group.CloudGroup;
import net.streamlinecloud.main.core.group.CloudGroupManager;
import net.streamlinecloud.main.core.server.CloudServer;
import net.streamlinecloud.main.utils.Cache;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GroupsController {

    public void create(@NotNull Context context) {
    }

    public void getAll(@NotNull Context context) {
        List<String> groups = new ArrayList<>();

        Cache.i().getActiveGroups().forEach(g -> groups.add(g.getName()));

        context.result(new Gson().toJson(groups));
        context.status(200);
    }


    public void get(@NotNull Context context) {
        String name = context.pathParam("name");
        StreamlineGroup group = CloudGroupManager.getInstance().getGroupByName(name);

        if (group != null) {
            group.setServerOnlineCount(CloudGroupManager.getInstance().getGroupOnlineServers(CloudGroupManager.getInstance().getGroupByName(group.getName())).size());
            context.result(new Gson().toJson(group, StreamlineGroup.class));
            context.status(200);
        } else {
            context.result("serverNotFound");
            context.status(601);
        }
    }

    public void servers(@NotNull Context context) {
        try {
            CloudGroup group = CloudGroupManager.getInstance().getGroupByName(context.pathParam("name"));
            if (group == null) {
                context.result("groupNotFound");
                context.status(200);
                return;
            }

            List<CloudServer> servers = CloudGroupManager.getInstance().getGroupOnlineServers(group);

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(CloudServer.class, new StreamlineServerSerializer())
                    .create();

            List<StreamlineServer> streamlineServers = new ArrayList<>(servers);

            context.result(gson.toJson(streamlineServers));
            context.status(200);
        } catch (Exception e) {
            StreamlineCloud.logError(e.getMessage());
        }
    }

    public void update(@NotNull Context context, @NotNull String s) {

    }

    public void delete(@NotNull Context context, @NotNull String s) {

    }

}
