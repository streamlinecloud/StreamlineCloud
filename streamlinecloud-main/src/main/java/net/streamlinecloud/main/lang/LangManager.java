package net.streamlinecloud.main.lang;

import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.utils.Cache;
import net.streamlinecloud.main.utils.Utils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.stream.Stream;

public class LangManager {

    public LangManager() {
        File langFile = new File(Cache.i().homeFile + "/data/lang");
        langFile.mkdirs();
        try {

            if (!Files.exists(new File(Cache.i().homeFile + "/data/lang/en.json").toPath())) Files.copy(Utils.getResourceFile("en.json", "json").toPath(), new File(Cache.i().homeFile + "/data/lang/en.json").toPath());
            if (!Files.exists(new File(Cache.i().homeFile + "/data/lang/de.json").toPath())) Files.copy(Utils.getResourceFile("de.json", "json").toPath(), new File(Cache.i().homeFile + "/data/lang/de.json").toPath());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //ReadLangFiles
        try {
            try (Stream<Path> paths = Files.walk(Paths.get(langFile.getPath()))) {
                paths
                        .filter(Files::isRegularFile)
                        .forEach(LangManager::readLangFile);
            }
        } catch (Exception e) {
            StreamlineCloud.log(e.getMessage());
        }
    }

    public static void readLangFile(Path file) {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            JSONObject configJson = new JSONObject(content.toString());

            HashMap<String, String> config = new HashMap<>();
            for (String key : configJson.keySet()) {
                config.put(key, configJson.getString(key));
            }

            Cache.i().getLanguages().add(new CloudLanguage(file.getFileName().toString(), config));
        } catch (IOException e) {
            StreamlineCloud.log(e.getMessage());
        }
    }
}
