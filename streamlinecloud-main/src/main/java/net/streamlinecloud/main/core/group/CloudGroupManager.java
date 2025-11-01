package net.streamlinecloud.main.core.group;

import lombok.Getter;
import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.server.RunningServer;
import net.streamlinecloud.main.core.server.RunningServerManager;
import net.streamlinecloud.main.lang.ReplacePaket;
import net.streamlinecloud.main.utils.Cache;
import net.streamlinecloud.main.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CloudGroupManager {

    @Getter
    private static CloudGroupManager instance;

    public CloudGroupManager() {
        instance = this;
    }

    public List<RunningServer> getGroupOnlineServers(CloudGroup g) {
        List<RunningServer> onlineServers = new ArrayList<>();
        for (RunningServer s : RunningServerManager.getInstance().getRunningServers()) {
            if (s.getGroup().equals(g.getName())) {
                onlineServers.add(s);
            }
        }
        return onlineServers;
    }

    /**
     * This functions saves and activates a new group.
     * @param group A new CloudGroup instance
     * @return true if the group was successfully created, false otherwise.
     */
    public boolean create(CloudGroup group) {

        if (group.getTemplates().isEmpty()) {
            List<String> templates = new ArrayList<>();
            templates.add("default/" + group.getName());
            group.setTemplates(templates);
        }

        try {
            group.save();
        } catch (IOException e) {
            StreamlineCloud.log("sc.command.groups.create.cantSave", new ReplacePaket[]{new ReplacePaket("%1", e.getMessage())});
            return false;
        }

        Utils.runMkdir(new File(Cache.i().homeFile + "/templates/" + group.getTemplates().getFirst()).mkdirs());

        if (group.getRuntime().equals(ServerRuntime.PROXY)) {
            try {
                Files.copy(Objects.requireNonNull(Utils.getResourceFile("velocity.toml", "")).toPath(), new File(Cache.i().homeFile + "/templates/" + group.getTemplates().getFirst() + "/velocity.toml").toPath());
                Files.writeString(Path.of(Cache.i().homeFile + "/templates/" + group.getTemplates().getFirst() + "/forwarding.secret"), StreamlineCloud.generateApiKey(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Cache.i().getActiveGroups().add(group);
        return true;
    }

    public CloudGroup getGroupByName(String name) {

        for (CloudGroup group : Cache.i().getActiveGroups()) {

            if (group.getName().equals(name)) {

                return group;
            }
        }
        return null;
    }

    public boolean groupExists(String name) {
        for (CloudGroup group : Cache.i().getActiveGroups()) {
            if (group.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
