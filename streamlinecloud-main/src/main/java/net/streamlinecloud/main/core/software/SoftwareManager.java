package net.streamlinecloud.main.core.software;

import lombok.Getter;
import lombok.Setter;
import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.api.software.StreamlineSoftware;
import net.streamlinecloud.main.config.StreamlineConfig;

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
        softwareConfig.software.add(new StreamlineSoftware(name, runtime, uri));

        config.setData(softwareConfig);
        config.save();
    }



}
