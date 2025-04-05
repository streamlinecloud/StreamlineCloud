package net.streamlinecloud.main.utils;

import net.streamlinecloud.main.utils.MainBuildConfig;

public class BuildSettings {

    public static String name = "StreamlineCloud";
    public static String version = "BETA_0.3";
    public static String authors = "Quinilo, creperozelot";
    public static String website = "https://streamlinecloud.net";
    public static boolean testBuild = false;

    public static String getVersionInfo() {
        return name + "#" + version + "#" + MainBuildConfig.BUILD_DATE + "#" + testBuild;
    }

}
