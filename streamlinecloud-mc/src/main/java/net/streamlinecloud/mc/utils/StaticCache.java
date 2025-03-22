package net.streamlinecloud.mc.utils;

import lombok.Getter;
import lombok.Setter;
import net.streamlinecloud.api.packet.StaticServerDataPacket;
import net.streamlinecloud.api.server.ServerRuntime;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StaticCache {

    public static String accessKey = null;
    public static File plFolder;
    public static boolean whitelistEnabled = false;
    public static List<String> whitelist = new ArrayList<>();

    @Getter @Setter
    private static ServerRuntime runtime;

    public static StaticServerDataPacket serverData;
}