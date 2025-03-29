package net.streamlinecloud.api.server;

import com.google.gson.*;

import java.lang.reflect.Type;

public class StreamlineServerSerializer implements JsonSerializer<StreamlineServer> {

    @Override
    public JsonElement serialize(StreamlineServer src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("name", src.getName());
        jsonObject.addProperty("ip", src.getIp());
        jsonObject.addProperty("port", src.getPort());
        jsonObject.addProperty("maxOnlineCount", src.getMaxOnlineCount());
        jsonObject.addProperty("serverUseState", src.getServerUseState().toString());
        jsonObject.addProperty("serverState", src.getServerState().toString());
        jsonObject.addProperty("group", src.getGroup());
        jsonObject.addProperty("uuid", src.getUuid());
        jsonObject.addProperty("rconUuid", src.getRconUuid());
        jsonObject.addProperty("runtime", src.getRuntime().toString());

        return jsonObject;
    }

}
