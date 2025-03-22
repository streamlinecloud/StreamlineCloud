package net.streamlinecloud.api.packet;

import lombok.Getter;

@Getter
public class VersionPacket {

    String version;
    String apiVersion;
    String buildDate;

    public VersionPacket(String version, String apiVersion, String buildDate) {
        this.version = version;
        this.apiVersion = apiVersion;
        this.buildDate = buildDate;
    }
}
