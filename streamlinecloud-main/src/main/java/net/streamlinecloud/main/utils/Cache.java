package net.streamlinecloud.main.utils;


import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.websocket.WsContext;
import io.streamlinemc.api.RestUtils.RconData;
import net.streamlinecloud.api.server.StreamlineServer;
import net.streamlinecloud.main.core.backend.remoteLogic.WSClient;
import net.streamlinecloud.main.core.backend.socket.ServerSocket;
import net.streamlinecloud.main.core.group.CloudGroup;
import net.streamlinecloud.main.lang.CloudLanguage;
import net.streamlinecloud.main.plugin.PluginManager;
import net.streamlinecloud.main.core.server.CloudServer;
import net.streamlinecloud.main.terminal.input.ConsoleQuestion;
import lombok.Getter;
import lombok.Setter;

import javax.websocket.Session;
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
    public boolean useLgecyColor = false;
    public Javalin backend;
    public final HashMap<String, RconData> rconDetails = new HashMap<>();
    public static int currentAnimationLine = 1;
    public static ServerSocket serverSocket;

    public static Cache i() {
        return i;
    }
}
