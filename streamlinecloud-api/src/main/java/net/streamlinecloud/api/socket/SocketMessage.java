package net.streamlinecloud.api.socket;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SocketMessage {

    SocketMessageType type;
    String content;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static SocketMessage fromJson(String json) {
        return new Gson().fromJson(json, SocketMessage.class);
    }

    @Getter
    public enum SocketMessageType {
        SUBSCRIBE_SERVER(true),
        SUBSCRIBE_GROUP(true),
        IAM(true),

        HEARTBEAT(false),
        SUCCESS(false),
        ERROR(false),
        MOVE_SERVER(false),
        SERVER_UPDATE(false);

        final boolean fromClient;

        SocketMessageType(boolean fromClient) {
            this.fromClient = fromClient;
        }
    }
}
