package net.streamlinecloud.main.utils;


import com.google.gson.Gson;
import io.javalin.Javalin;
import io.streamlinemc.api.RestUtils.RconData;
import net.streamlinecloud.main.config.MainConfig;
import net.streamlinecloud.main.core.backend.socket.RemoteSocket;
import net.streamlinecloud.main.core.backend.socket.ServerSocket;
import net.streamlinecloud.main.core.group.CloudGroup;
import net.streamlinecloud.main.lang.CloudLanguage;
import net.streamlinecloud.main.extension.ExtensionManager;
import net.streamlinecloud.main.core.server.CloudServer;
import net.streamlinecloud.main.terminal.input.ConsoleQuestion;
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
    public final List<CloudServer> serversWaitingForStart = new ArrayList<>();
    public List<ConsoleQuestion> consoleInputs = new ArrayList<>();
    public CloudGroup defaultGroup;
    public String apiKey;
    public MainConfig config;
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
    public RemoteSocket webSocketClient = null;
    public ExtensionManager pluginManager = new ExtensionManager();
    public boolean disabledColors = false;
    public boolean useLgecyColor = false;
    public Javalin backend;
    public final HashMap<String, RconData> rconDetails = new HashMap<>();
    public int currentAnimationLine = 1;
    public ServerSocket serverSocket;
    public int networkPlayerCount = 0;

    public static Cache i() {
        return i;
    }
}
