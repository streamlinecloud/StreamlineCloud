package net.streamlinecloud.main.utils;


import com.google.gson.Gson;
import io.javalin.Javalin;
import net.streamlinecloud.api.rest.RconData;
import net.streamlinecloud.api.util.StreamlineState;
import net.streamlinecloud.main.config.MainConfig;
import net.streamlinecloud.main.backend.socket.RemoteSocket;
import net.streamlinecloud.main.backend.socket.ServerSocket;
import net.streamlinecloud.main.extension.ExtensionManager;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.*;

@Getter @Setter
public class Cache {

    private static Cache i;

    public Cache() {
        i = this;
    }
    
    public List<String> arguments = new ArrayList<>();
    public List<String> dataCache = new ArrayList<>();
    public String apiKey;
    public MainConfig config;
    public boolean debugMode = false;
    public String currentScreenServerName;
    public File homeFile = new File(System.getProperty("user.dir"));
    public String pluginVersion = "unknown";
    public String pluginApiVersion = "unknown";
    public String pluginBuildDate = "unknown";
    public long startUptime = 0L;
    public boolean firstLaunch = false;
    public StreamlineState streamlineState;
    public Gson gson = new Gson().newBuilder().create();
    public RemoteSocket webSocketClient = null;
    public ExtensionManager pluginManager = new ExtensionManager();
    public boolean disabledColors = false;
    public boolean useLegacyColor = false;
    public Javalin backend;
    public final HashMap<String, RconData> rconDetails = new HashMap<>();
    public int currentAnimationLine = 1;
    public ServerSocket serverSocket;
    public int networkPlayerCount = 0;

    public static Cache i() {
        return i;
    }
}
