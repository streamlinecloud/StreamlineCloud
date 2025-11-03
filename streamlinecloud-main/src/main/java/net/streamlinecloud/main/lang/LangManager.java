package net.streamlinecloud.main.lang;

import lombok.Getter;
import lombok.Setter;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Getter
public class LangManager {

    @Getter
    private static LangManager instance;

    @Setter
    CloudLanguage currentLanguage = null;
    List<CloudLanguage> languages = new ArrayList<>();

    public LangManager() {
        instance = this;

        File langFile = new File(Cache.i().homeFile + "/data/lang");
        Utils.runMkdir(langFile.mkdir());
        try {

            if (!Files.exists(new File(Cache.i().homeFile + "/data/lang/en.json").toPath())) Files.copy(Objects.requireNonNull(Utils.getResourceFile("lang/en.json", "json")).toPath(), new File(Cache.i().homeFile + "/data/lang/en.json").toPath());
            if (!Files.exists(new File(Cache.i().homeFile + "/data/lang/de.json").toPath())) Files.copy(Objects.requireNonNull(Utils.getResourceFile("lang/de.json", "json")).toPath(), new File(Cache.i().homeFile + "/data/lang/de.json").toPath());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //ReadLangFiles
        try {
            try (Stream<Path> paths = Files.walk(Paths.get(langFile.getPath()))) {
                paths
                        .filter(Files::isRegularFile)
                        .forEach(this::readLangFile);
            }
        } catch (Exception e) {
            StreamlineCloud.log(e.getMessage());
        }
    }

    public void readLangFile(Path file) {
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

            languages.add(new CloudLanguage(file.getFileName().toString(), config));
        } catch (IOException e) {
            StreamlineCloud.log(e.getMessage());
        }
    }
}
