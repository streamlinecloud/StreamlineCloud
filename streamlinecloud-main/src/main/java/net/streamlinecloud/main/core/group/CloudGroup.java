package net.streamlinecloud.main.core.group;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import net.streamlinecloud.api.group.StreamlineGroup;
import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.main.utils.Cache;
import lombok.Getter;
import net.streamlinecloud.main.utils.Utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Getter
public class CloudGroup extends StreamlineGroup implements Cloneable {

    public CloudGroup(String name, int minOnlineCount, List<String> templates, ServerRuntime runtime, String software) {
        setName(name);
        setJavaExec("%default");
        setMinOnlineCount(minOnlineCount);
        setTemplates(templates);
        setRuntime(runtime);
        setSoftwareName(software);
    }

    public void save() throws IOException {

        File file  = new File(Cache.i().homeFile + "/groups/" + getName() + ".json");

        Utils.runMkdir(file.createNewFile());

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

    @Override
    public CloudGroup clone() {
        try {
            return (CloudGroup) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
