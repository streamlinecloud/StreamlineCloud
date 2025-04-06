package net.streamlinecloud.main.utils;

import net.streamlinecloud.main.utils.MainBuildConfig;

public class Settings {

    public static String name = "StreamlineCloud";
    public static String authors = "Quinilo, creperozelot";
    public static String website = "https://streamlinecloud.net";
    public static boolean testBuild = false;

    public static String getVersionInfo() {
        return name + "#" + MainBuildConfig.VERSION + "#" + MainBuildConfig.BUILD_DATE + "#" + testBuild;
    }

}
