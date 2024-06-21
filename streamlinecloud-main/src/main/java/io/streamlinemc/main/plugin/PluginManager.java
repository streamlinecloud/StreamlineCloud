package io.streamlinemc.main.plugin;

import io.streamlinemc.api.plmanager.StreamlinePlugin;
import io.streamlinemc.api.plmanager.command.CommandManager;
import io.streamlinemc.api.plmanager.event.EventManager;
import io.streamlinemc.main.StreamlineCloud;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class PluginManager {

    public static final EventManager eventManager = new EventManager();
    public static final CommandManager commandManager = new CommandManager();
    private List<StreamlinePlugin> pluginList = new ArrayList<>();
    private File pluginsFolder = new File(System.getProperty("user.dir") + "/plugins");
    public void loadPlugins() {
        pluginsFolder.mkdirs();


        if (pluginsFolder.exists() && pluginsFolder.isDirectory()) {
            File[] files = pluginsFolder.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".jar")) {
                        PluginConfig config = extractPluginConfig(file);

                        if (config != null) {
                            loadPlugin(file, config);
                        }
                    }
                }
            }
        }
    }

    private PluginConfig extractPluginConfig(File jarFile) {
        try (JarFile jar = new JarFile(jarFile)) {
            JarEntry entry = jar.getJarEntry("plugin.yml");

            if (entry != null) {
                try (InputStream input = jar.getInputStream(entry)) {
                    Yaml yaml = new Yaml();
                    return yaml.loadAs(input, PluginConfig.class);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle error: Failed to extract or parse the plugin.yml
        }

        return null;
    }

    private void loadPlugin(File file, PluginConfig config) {
        try {
            URLClassLoader classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()});
            Class<?> pluginClass = classLoader.loadClass(config.getMainClass());

            if (StreamlinePlugin.class.isAssignableFrom(pluginClass)) {
                StreamlinePlugin plugin = (StreamlinePlugin) pluginClass.getDeclaredConstructor().newInstance();
                pluginList.add(plugin);

                StreamlineCloud.log("§GOLDLoaded plugin: " + config.getName() + " by " + config.getAuthor() + " version " + config.getVersion());
            } else {
                // Handle error: The class doesn't implement StreamlinePlugin
                StreamlineCloud.log("There are Files that are not Streamline Plugins.");
            }
        } catch (Exception e) {
            StreamlineCloud.log("There was an error while loading a plugin.");
            e.printStackTrace();
        }
    }

    public void executeStartup() {
        for (StreamlinePlugin streamlinePlugin : pluginList) {
            streamlinePlugin.start(eventManager, commandManager);
        }
    }

    public void executeStop() {
        for (StreamlinePlugin streamlinePlugin : pluginList) {
            streamlinePlugin.shutdown();
        }
    }

}
