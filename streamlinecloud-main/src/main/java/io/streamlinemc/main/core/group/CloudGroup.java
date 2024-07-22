package io.streamlinemc.main.core.group;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import io.streamlinemc.api.group.StreamlineGroup;
import io.streamlinemc.api.server.ServerRuntime;
import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.utils.Cache;
import io.streamlinemc.main.utils.Downloader;
import lombok.Getter;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Getter
public class CloudGroup extends StreamlineGroup {

    public CloudGroup(String name, String javaExec, int minOnlineCount, List<String> templates, ServerRuntime runtime) {
        setName(name);
        setJavaExec(javaExec);
        setMinOnlineCount(minOnlineCount);
        setTemplates(templates);
        setRuntime(runtime);
    }

    public void save() throws IOException {

        File file  = new File(Cache.i().homeFile + "/groups/" + getName() + ".json");

        file.createNewFile();

        String json = new Gson().toJson(this, CloudGroup.class);
        JsonWriter writer = new JsonWriter(new FileWriter(file));
        writer.jsonValue(json);
        writer.flush();
    }

    public void delete() {

        File file  = new File(Cache.i().homeFile + "/groups/" + getName() + ".json");

        try {
            Files.delete(Paths.get(file.getPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Cache.i().getActiveGroups().remove(this);
    }

    public interface DownloadResponse {

        void execute(boolean success);

    }
}
