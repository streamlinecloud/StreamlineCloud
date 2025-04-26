package net.streamlinecloud.main.config;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.streamlinecloud.main.StreamlineCloud;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

@Getter
@Setter
@AllArgsConstructor
public class StreamlineConfig {

    Object data;
    String path;

    private final Gson gson = new Gson().newBuilder()
            .setPrettyPrinting()
            .create();


    @SneakyThrows
    public void init() {
        File file = new File(getPath());

        if (!file.exists()) {
            file.createNewFile();
            save();
        }

        String jsonConfig = FileUtils.readFileToString(file, Charset.defaultCharset());
        Object config = gson.fromJson(jsonConfig, getData().getClass());
        setData(config);
    }

    public void save() {
        String json = gson.toJson(getData(), getData().getClass());
        File config = new File(getPath());
        try {
            FileUtils.writeStringToFile(config, json);
        } catch (IOException e) {
            StreamlineCloud.logError(e.getMessage());
        }
    }
}
