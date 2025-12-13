package net.streamlinecloud.api.socket;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.streamlinecloud.api.player.StreamlinePlayer;

@Getter
@RequiredArgsConstructor
public class SocketMessage {

    @NonNull
    SocketMessageType type;

    @NonNull
    String content;

    StreamlinePlayer player;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static SocketMessage fromJson(String json) {
        return new Gson().fromJson(json, SocketMessage.class);
    }

    public SocketMessage setPlayer(StreamlinePlayer player) {
        this.player = player;
        return this;
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
        SERVER_UPDATE(false),
        WHITELIST_UPDATE(false),

        PLAYER_CONNECT(false),
        PLAYER_MESSAGE(false),
        PLAYER_KICK(false);

        final boolean fromClient;

        SocketMessageType(boolean fromClient) {
            this.fromClient = fromClient;
        }
    }
}
