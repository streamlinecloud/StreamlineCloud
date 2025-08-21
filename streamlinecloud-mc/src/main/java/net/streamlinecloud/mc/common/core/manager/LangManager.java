package net.streamlinecloud.mc.common.core.manager;

import com.google.gson.Gson;
import lombok.Getter;
import net.streamlinecloud.mc.common.utils.BackendRequest;

import java.util.HashMap;

/**
 * This class grants access to language strings from the StreamlineCloud main module
 */
public class LangManager {

    @Getter
    private static LangManager instance;
    private HashMap<String, String> messages = new HashMap<>();

    public LangManager() {
        instance = this;
    }

    /**
     * This method fetches translations from the StreamlineCloud main module in the  default language
     * @param keys A list of translation keys that are being added to the language manager
     */
    public void fetch(String[] keys) {
        new BackendRequest("translation").setType(BackendRequest.RestType.POST).withBody(new Gson().toJson(keys)).fetch(request -> {
            messages.putAll( new Gson().fromJson(request.getResponse(), HashMap.class));
        });
    }

    /**
     * The key needs to be fetched via the {@link #fetch(String[])} function first
     * @param key The key of the translation string
     */
    public String get(String key) {
        return messages.getOrDefault(key, "unknown");
    }
}
