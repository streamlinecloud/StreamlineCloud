package net.streamlinecloud.mc.common.utils;

import com.google.gson.Gson;
import net.streamlinecloud.api.packet.StaticServerDataPacket;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.Charset;

public class Functions {

    public static void startup() {
        try {
            StaticCache.plFolder = new File(System.getProperty("user.dir"));
            File key = new File(StaticCache.plFolder.getAbsolutePath() + "/.apikey");

            String keyString = FileUtils.readFileToString(key, Charset.defaultCharset());

            StaticCache.accessKey = keyString.split(",_,")[0];
            StaticCache.serverData = new Gson().fromJson(keyString.split(",_,")[1], StaticServerDataPacket.class);

            FileUtils.forceDelete(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}