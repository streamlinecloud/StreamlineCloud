package net.streamlinecloud.mc.common.utils;

import lombok.Getter;
import lombok.Setter;
import net.streamlinecloud.api.packet.StaticServerDataPacket;
import net.streamlinecloud.api.packet.WhitelistConfigurationPacket;
import net.streamlinecloud.api.server.ServerRuntime;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StaticCache {

    public static String accessKey = null;
    public static File plFolder;
    public static WhitelistConfigurationPacket whitelist = new WhitelistConfigurationPacket(false, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

    @Getter @Setter
    private static ServerRuntime runtime;

    public static StaticServerDataPacket serverData;
}