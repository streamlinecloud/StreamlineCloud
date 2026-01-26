package net.streamlinecloud.main.core.software;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.api.software.StreamlineSoftware;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.config.StreamlineConfig;
import net.streamlinecloud.main.core.server.RunningServer;
import net.streamlinecloud.main.utils.Cache;
import net.streamlinecloud.main.utils.Utils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Getter @Setter
public class SoftwareManager {

    @Getter
    private static SoftwareManager instance;

    public StreamlineConfig config;

    List<SoftwareCatalogItem> catalog = new ArrayList<>();

    @SneakyThrows
    public SoftwareManager() {
        instance = this;

        try {
            Utils.runMkdir(new File(Cache.i().getHomeFile() + "/data/software").mkdirs());
            Files.copy(Objects.requireNonNull(Utils.getResourceFile("software_catalog.json", "json")).toPath(), new File(Cache.i().getHomeFile() + "/data/software/catalog.json").toPath());
        } catch (FileAlreadyExistsException ignored) {}

        loadCatalog();
    }

    public void loadCatalog() {
        List<SoftwareCatalogItem> list = new ArrayList<>();
        StreamlineConfig catalogConfig = new StreamlineConfig(list, Cache.i().getHomeFile() + "/data/software/catalog.json");
        catalogConfig.init();
        Type type = new TypeToken<List<SoftwareCatalogItem>>() {}.getType();
        catalog = new Gson().fromJson(new Gson().toJson(catalogConfig.getData()), type);
    }

    public StreamlineSoftware add(String name, ServerRuntime runtime, String fileUri) {
        SoftwareConfig softwareConfig = (SoftwareConfig) config.getData();
        Utils.runMkdir(new File(Cache.i().getHomeFile() + "/data/software/" + name).mkdirs());
        AtomicReference<String> uri = new AtomicReference<>(fileUri);


        if (uri.get().startsWith("http://") || uri.get().startsWith("https://")) {
            StreamlineCloud.download(uri.get(),Cache.i().getHomeFile() + "/data/software/" + name, success -> {
            });
        }

        StreamlineSoftware software = new StreamlineSoftware(name, runtime, name, false);
        softwareConfig.software.add(software);

        config.setData(softwareConfig);
        config.save();

        return software;
    }

    public StreamlineSoftware add(SoftwareCatalogItem catalogItem) {
        ServerRuntime runtime = ServerRuntime.SERVER;
        if (catalogItem.getSoftware().contains("velocity")) runtime = ServerRuntime.PROXY;
        return add(catalogItem.getSoftware() + "-" + catalogItem.getVersion(), runtime, catalogItem.getUrl());
    }

    public SoftwareCatalogItem getLatestSoftware(String software) {
        AtomicReference<SoftwareCatalogItem> result = new AtomicReference<>();
        SoftwareConfig softwareConfig = (SoftwareConfig) config.getData();
        for (SoftwareCatalogItem streamlineSoftware : catalog) {
            if (streamlineSoftware.getSoftware().startsWith(software)) {
                String ver = streamlineSoftware.getVersion();

                if (result.get() == null) result.set(streamlineSoftware);
                if (ver == null) continue;

                int i = 0;
                for (String subVer : ver.split("\\.")) {
                    try {
                        if (Integer.parseInt(subVer) > Integer.parseInt(result.get().getVersion().split("\\.")[i])) {
                            result.set(streamlineSoftware);
                        }
                    } catch (NumberFormatException e) {
                        continue;
                    }
                    i++;
                }
            }
        }
        return result.get();
    }

    @SneakyThrows
    public void delete(String name) {
        StreamlineSoftware software = getSoftware(name);
        if (software == null) return;

        FileUtils.delete(new File(Cache.i().getHomeFile() + "/data/software/" + software.getFolder()));

        SoftwareConfig softwareConfig = (SoftwareConfig) config.getData();
        softwareConfig.software.removeIf(s -> s.getName().equals(name));
        getConfig().save();
    }

    public void replace(String name, StreamlineSoftware software) {
        SoftwareConfig softwareConfig = (SoftwareConfig) config.getData();
        for (StreamlineSoftware s : softwareConfig.software) {
            if (s.getName().equals(name)) {
                s.setCached(software.isCached());
                s.setName(software.getName());
                s.setFolder(software.getFolder());
                s.setType(software.getType());
            }
        }
        getConfig().save();
    }

    public StreamlineSoftware getSoftware(String name ) {
        SoftwareConfig softwareConfig = (SoftwareConfig) config.getData();
        for (StreamlineSoftware software : softwareConfig.software) {
            if (software.getName().equals(name)) {
                return software;
            }
        }
        return null;
    }

    @SneakyThrows
    public void copyCache(String softwareName, RunningServer server) {
        StreamlineSoftware software = getSoftware(softwareName);
        if (software == null) return;
        if (software.isCached()) return;

        Utils.runMkdir(new File(server.getServerFolder() + "/cache").mkdirs());
        FileUtils.copyDirectory(new File(server.getServerFolder() + "/cache"), new File(Cache.i().getHomeFile() + "/data/software/" + software.getFolder() + "/cache"));

        software.setCached(true);
        replace(softwareName, software);
    }



}
