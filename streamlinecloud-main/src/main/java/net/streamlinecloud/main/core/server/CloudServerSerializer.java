package net.streamlinecloud.main.core.server;

import com.google.gson.*;

import java.lang.reflect.Type;

public class CloudServerSerializer implements JsonSerializer<CloudServer> {

    @Override
    public JsonElement serialize(CloudServer src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("name", src.getName());
        jsonObject.addProperty("startupTime", src.getStartupTime());
        jsonObject.addProperty("serverState", src.getServerState().toString());
        jsonObject.addProperty("onlinePlayers", new Gson().toJson(src.getOnlinePlayers()));
        jsonObject.addProperty("maxOnlineCount", src.getMaxOnlineCount());

        return jsonObject;
    }

}
