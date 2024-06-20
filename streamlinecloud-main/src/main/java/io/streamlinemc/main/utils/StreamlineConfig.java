package io.streamlinemc.main.utils;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class StreamlineConfig {

    int defaultProxyPort;
    int communicationBridgePort;

    String language;
    String defaultJavaPath;
    String fallbackGroup;

    boolean useWebSocket = false;
    String websocketUrl = "http://localhost:3000";

    boolean useMultiRoot = false;
    String multiRootConnection = "";

    boolean disableColors = false;
    boolean enableRconSupport = true;

    public static StreamlineConfig fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, StreamlineConfig.class);
    }

    public StreamlineConfig(String defaultJavaPath, int defaultProxyPort, int communicationBridgePort, String fallbackGroup) {
        this.defaultJavaPath = defaultJavaPath;
        this.defaultProxyPort = defaultProxyPort;
        this.communicationBridgePort = communicationBridgePort;
        this.fallbackGroup = fallbackGroup;
        this.language = "en.json";
    }
}
