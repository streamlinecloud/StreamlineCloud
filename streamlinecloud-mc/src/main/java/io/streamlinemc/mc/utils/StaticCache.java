package io.streamlinemc.mc.utils;

import io.streamlinemc.api.packet.StaticServerDataPacket;
import io.streamlinemc.api.server.ServerRuntime;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

public class StaticCache {

    public static String accessKey = null;
    public static File plFolder;

    @Getter  @Setter
    private static ServerRuntime runtime;

    public static StaticServerDataPacket serverData;
}