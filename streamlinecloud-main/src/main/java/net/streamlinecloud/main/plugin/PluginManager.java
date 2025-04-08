package net.streamlinecloud.main.plugin;

import net.streamlinecloud.api.plugin.StreamlinePlugin;
import net.streamlinecloud.api.plugin.command.CommandManager;
import net.streamlinecloud.api.plugin.event.EventManager;
import net.streamlinecloud.main.StreamlineCloud;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class PluginManager {

    public static final EventManager eventManager = new EventManager();
    public static final CommandManager commandManager = new CommandManager();
    private final List<StreamlinePlugin> pluginList = new ArrayList<>();
    private final File pluginsFolder = new File(System.getProperty("user.dir") + "/plugins");
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
            StreamlineCloud.logError(e.getMessage());
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
