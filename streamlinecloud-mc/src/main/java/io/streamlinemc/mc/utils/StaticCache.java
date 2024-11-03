package io.streamlinemc.mc.utils;

import io.streamlinemc.api.packet.StaticServerDataPacket;
import io.streamlinemc.api.server.ServerRuntime;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StaticCache {

    public static String accessKey = null;
    public static File plFolder;
    public static boolean whitelistEnabled = false;
    public static List<String> whitelist = new ArrayList<>();

    private static ServerRuntime runtime;

    public static StaticServerDataPacket serverData;

    public static void setRuntime(ServerRuntime runtime) {
        StaticCache.runtime = runtime;
    }

    public static ServerRuntime getRuntime() {
        return runtime;
    }
}