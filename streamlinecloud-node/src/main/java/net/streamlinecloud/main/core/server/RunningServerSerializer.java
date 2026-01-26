package net.streamlinecloud.main.core.server;

import com.google.gson.*;

import java.lang.reflect.Type;

public class RunningServerSerializer implements JsonSerializer<RunningServer> {

    @Override
    public JsonElement serialize(RunningServer src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("name", src.getName());
        jsonObject.addProperty("startupTime", src.getStartupTime());
        jsonObject.addProperty("serverState", src.getServerState().toString());
        jsonObject.addProperty("onlinePlayers", new Gson().toJson(src.getOnlinePlayers()));
        jsonObject.addProperty("maxOnlineCount", src.getMaxOnlineCount());

        return jsonObject;
    }

}
