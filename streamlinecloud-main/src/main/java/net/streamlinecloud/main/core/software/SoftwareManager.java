package net.streamlinecloud.main.core.software;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.api.software.StreamlineSoftware;
import net.streamlinecloud.main.config.StreamlineConfig;
import net.streamlinecloud.main.core.server.CloudServer;
import net.streamlinecloud.main.utils.Cache;
import org.apache.commons.io.FileUtils;

import java.io.File;

@Getter @Setter
public class SoftwareManager {

    @Getter
    private static SoftwareManager instance;

    public StreamlineConfig config;

    public SoftwareManager() {
        instance = this;
    }

    public void add(String name, ServerRuntime runtime, String uri) {
        SoftwareConfig softwareConfig = (SoftwareConfig) config.getData();
        softwareConfig.software.add(new StreamlineSoftware(name, runtime, uri, false));

        config.setData(softwareConfig);
        config.save();
    }

    public StreamlineSoftware getSoftware(String name ) {
        SoftwareConfig softwareConfig = (SoftwareConfig) config.getData();
        for (StreamlineSoftware software : softwareConfig.software) {
            if (software.getName().equals(name)) {
                return software;
            }
        }
        return null;
    }

    @SneakyThrows
    public void copyCache(String softwareName, CloudServer server) {
        StreamlineSoftware software = getSoftware(softwareName);
        if (software == null) return;
        if (software.isCached()) return;

        new File(server.getServerFolder() + "/cache").mkdirs();
        FileUtils.copyDirectory(new File(server.getServerFolder() + "/cache"), new File(Cache.i().getHomeFile() + "/data/software/" + software.getFolder() + "/cache"));

        SoftwareConfig softwareConfig = (SoftwareConfig) config.getData();
        for (StreamlineSoftware s : softwareConfig.software) {
            if (s.getName().equals(software.getName())) {
                s.setCached(true);
            }
        }

        getConfig().save();
    }



}
