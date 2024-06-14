package io.streamlinemc.main.utils;


import com.google.gson.Gson;
import io.streamlinemc.main.core.backend.remoteLogic.WSClient;
import io.streamlinemc.main.core.group.CloudGroup;
import io.streamlinemc.main.lang.CloudLanguage;
import io.streamlinemc.main.plugin.PluginManager;
import io.streamlinemc.main.core.server.CloudServer;
import io.streamlinemc.main.terminal.input.ConsoleInput;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StaticCache {

    @Getter @Setter
    private static List<String> arguments = new ArrayList<>();

    @Getter
    private static List<String> dataCache = new ArrayList<>();

    @Getter
    private static final List<CloudServer> runningServers = new ArrayList<>();

    @Getter
    private static final List<CloudGroup> activeGroups = new ArrayList<>();

    @Getter
    private static final HashMap<CloudServer, CloudGroup> linkedServers = new HashMap<>();

    @Getter
    private static final List<CloudServer> serversWaitingForStart = new ArrayList<>();

    @Getter @Setter
    private static List<ConsoleInput> consoleInputs = new ArrayList<>();

    @Getter @Setter
    private static CloudGroup defaultGroup;

    @Getter @Setter
    private static String apiKey;

    @Getter @Setter
    private static StreamlineConfig config;

    @Getter @Setter
    private static List<CloudLanguage> languages = new ArrayList<>();

    @Getter @Setter
    private static CloudLanguage currentLanguage;

    @Getter @Setter
    private static boolean debugMode = false;

    @Getter @Setter
    private static String currentScreenServerName;

    @Getter @Setter
    private static String pluginVersion = "unknown";
    @Getter @Setter
    private static String pluginApiVersion = "unknown";
    @Getter @Setter
    private static String pluginBuildDate = "unknown";
    @Getter @Setter
    private static long startuptime = 0L;

    @Getter @Setter
    private static boolean firstLaunch = false;

    @Getter
    private static Gson gson = new Gson().newBuilder().setPrettyPrinting().create();

    @Getter @Setter
    private static WSClient webSocketClient = null;

    @Getter @Setter
    private static PluginManager pluginManager = new PluginManager();

    @Getter @Setter
    private static boolean disabledColors = false;

}
