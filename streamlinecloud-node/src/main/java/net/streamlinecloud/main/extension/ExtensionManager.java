package net.streamlinecloud.main.extension;

import net.streamlinecloud.api.extension.StreamlineExtension;
import net.streamlinecloud.api.extension.command.CommandManager;
import net.streamlinecloud.api.extension.event.EventManager;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.utils.Cache;
import net.streamlinecloud.main.utils.Utils;
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
    private final File extensionsFolder = new File(System.getProperty("user.dir") + "/extensions");
    public void loadExtensions() {
        Utils.runMkdir(extensionsFolder.mkdirs());


        if (extensionsFolder.exists() && extensionsFolder.isDirectory()) {
            File[] files = extensionsFolder.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".jar")) {
                        ExtensionConfig config = extractextensionConfig(file);

                        if (config != null) {
                            loadExtension(file, config);
                        }
                    }
                }
            }
        }
    }

    private ExtensionConfig extractextensionConfig(File jarFile) {
        try (JarFile jar = new JarFile(jarFile)) {
            JarEntry entry = jar.getJarEntry("extension.yml");

            if (entry != null) {
                try (InputStream input = jar.getInputStream(entry)) {
                    Yaml yaml = new Yaml();
                    return yaml.loadAs(input, ExtensionConfig.class);
                }
            }
        } catch (IOException e) {
            StreamlineCloud.getLogger().error(e.getMessage());
        }

        return null;
    }

    private void loadExtension(File file, ExtensionConfig config) {
        try {
            URLClassLoader classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()});
            Class<?> extensionClass = classLoader.loadClass(config.getMainClass());

            if (StreamlineExtension.class.isAssignableFrom(extensionClass)) {
                StreamlineExtension extension = (StreamlineExtension) extensionClass.getDeclaredConstructor().newInstance();
                extensionList.put(extension, config);

                StreamlineCloud.log("Enabling " + config.getId() + "_v" + config.getVersion() + " by " + config.getAuthor());
            } else {
                StreamlineCloud.log("There are Files that are not Streamline extensions.");
            }
        } catch (Exception e) {
            StreamlineCloud.log("There was an error while loading a extension. (" + e.getMessage() + ")");
        }
    }

    public void executeStartup() {
        for (StreamlineExtension streamlineExtension : extensionList.keySet()) {
            File dataFolder = new File(Cache.i().homeFile + "/data/extension/" + extensionList.get(streamlineExtension).getId());
            Utils.runMkdir(dataFolder.mkdirs());
            try {
                streamlineExtension.initialize(eventManager, commandManager, dataFolder);
            } catch (Exception e) {
                StreamlineCloud.getLogger().error("Failed to enable " + extensionList.get(streamlineExtension).name + " - " +  e.getMessage());
                streamlineExtension.disable();
            }
        }
    }

    public void executeStop() {
        for (StreamlineExtension streamlineextension : extensionList.keySet()) {
            streamlineextension.disable();
        }
    }

}
