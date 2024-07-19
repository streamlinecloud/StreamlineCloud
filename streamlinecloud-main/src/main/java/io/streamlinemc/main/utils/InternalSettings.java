package io.streamlinemc.main.utils;

public class InternalSettings {

    public static String name = "";
    public static String version = "BETA_0.1";
    public static String authors = "Quinilo, creperozelot";
    public static String buildDate = "11.11.2023";
    public static String defaultJavaPath = "/home/quinilo/.sdkman/candidates/java/current/bin/java";
    public static boolean testBuild = false;

    public static String getVersionInfo() {
        return name + "#" + version + "#" + buildDate + "#" + testBuild;
    }

}
