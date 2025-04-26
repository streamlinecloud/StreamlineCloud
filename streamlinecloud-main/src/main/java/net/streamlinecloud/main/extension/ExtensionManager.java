package net.streamlinecloud.main.extension;

import net.streamlinecloud.api.extension.StreamlineExtension;
import net.streamlinecloud.api.extension.command.CommandManager;
import net.streamlinecloud.api.extension.event.EventManager;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.utils.Cache;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class ExtensionManager {

    public static final EventManager eventManager = new EventManager();
    public static final CommandManager commandManager = new CommandManager();
    private final HashMap<StreamlineExtension, ExtensionConfig> extensionList = new HashMap<>();
    private final File pluginsFolder = new File(System.getProperty("user.dir") + "/plugins");
    public void loadPlugins() {
        pluginsFolder.mkdirs();


        if (pluginsFolder.exists() && pluginsFolder.isDirectory()) {
            File[] files = pluginsFolder.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".jar")) {
                        ExtensionConfig config = extractPluginConfig(file);

                        if (config != null) {
                            loadPlugin(file, config);
                        }
                    }
                }
            }
        }
    }

    private ExtensionConfig extractPluginConfig(File jarFile) {
        try (JarFile jar = new JarFile(jarFile)) {
            JarEntry entry = jar.getJarEntry("plugin.yml");

            if (entry != null) {
                try (InputStream input = jar.getInputStream(entry)) {
                    Yaml yaml = new Yaml();
                    return yaml.loadAs(input, ExtensionConfig.class);
                }
            }
        } catch (IOException e) {
            StreamlineCloud.logError(e.getMessage());
        }

        return null;
    }

    private void loadPlugin(File file, ExtensionConfig config) {
        try {
            URLClassLoader classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()});
            Class<?> pluginClass = classLoader.loadClass(config.getMainClass());

            if (StreamlineExtension.class.isAssignableFrom(pluginClass)) {
                StreamlineExtension plugin = (StreamlineExtension) pluginClass.getDeclaredConstructor().newInstance();
                extensionList.put(plugin, config);

                StreamlineCloud.log("Enabling " + config.getId() + "_v" + config.getVersion() + " by " + config.getAuthor());
            } else {
                StreamlineCloud.log("There are Files that are not Streamline Plugins.");
            }
        } catch (Exception e) {
            StreamlineCloud.log("There was an error while loading a plugin.");
            e.printStackTrace();
        }
    }

    public void executeStartup() {
        for (StreamlineExtension streamlineExtension : extensionList.keySet()) {
            File dataFolder = new File(Cache.i().homeFile + "/data/plugin/" + extensionList.get(streamlineExtension).getId());
            dataFolder.mkdirs();
            streamlineExtension.initialize(eventManager, commandManager, dataFolder);
        }
    }

    public void executeStop() {
        for (StreamlineExtension streamlinePlugin : extensionList.keySet()) {
            streamlinePlugin.disable();
        }
    }

}
