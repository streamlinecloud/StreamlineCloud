package io.streamlinemc.main.utils;


import com.google.gson.Gson;
import io.javalin.Javalin;
import io.streamlinemc.api.RestUtils.RconData;
import io.streamlinemc.main.core.backend.remoteLogic.WSClient;
import io.streamlinemc.main.core.group.CloudGroup;
import io.streamlinemc.main.lang.CloudLanguage;
import io.streamlinemc.main.plugin.PluginManager;
import io.streamlinemc.main.core.server.CloudServer;
import io.streamlinemc.main.terminal.input.ConsoleQuestion;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter @Setter
public class Cache {

    private static Cache i;

    public Cache() {
        i = this;
    }
    
    public List<String> arguments = new ArrayList<>();
    public List<String> dataCache = new ArrayList<>();
    public final List<CloudServer> runningServers = new ArrayList<>();
    public final List<CloudGroup> activeGroups = new ArrayList<>();
    public final HashMap<CloudServer, CloudGroup> linkedServers = new HashMap<>();
    public final List<CloudServer> serversWaitingForStart = new ArrayList<>();
    public List<ConsoleQuestion> consoleInputs = new ArrayList<>();
    public CloudGroup defaultGroup;
    public String apiKey;
    public StreamlineConfig config;
    public List<CloudLanguage> languages = new ArrayList<>();
    public CloudLanguage currentLanguage;
    public boolean debugMode = false;
    public String currentScreenServerName;
    public File homeFile = new File(System.getProperty("user.dir"));
    public String pluginVersion = "unknown";
    public String pluginApiVersion = "unknown";
    public String pluginBuildDate = "unknown";
    public long startuptime = 0L;
    public boolean firstLaunch = false;
    public Gson gson = new Gson().newBuilder().create();
    public WSClient webSocketClient = null;
    public PluginManager pluginManager = new PluginManager();
    public boolean disabledColors = false;
    public Javalin backend;
    public final HashMap<String, RconData> rconDetails = new HashMap<>();

    public static Cache i() {
        return i;
    }
}
