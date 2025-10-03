package net.streamlinecloud.main.core.backend.controller;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import net.streamlinecloud.api.player.StreamlinePlayer;
import net.streamlinecloud.api.socket.SocketMessage;
import net.streamlinecloud.main.core.server.RunningServerManager;
import net.streamlinecloud.main.utils.PlayerRegister;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerController {

    public void get(@NotNull Context ctx) {
        StreamlinePlayer player = PlayerRegister.getInstance().get(UUID.fromString(ctx.pathParam("uuid")));

        if (player == null) {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.result("Player not online");
            return;
        }

        ctx.result(new Gson().toJson(player));
        ctx.status(HttpStatus.OK);
    }

    public void getByName(@NotNull Context ctx) {
        StreamlinePlayer player = PlayerRegister.getInstance().get(ctx.pathParam("name"));

        if (player == null) {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.result("Player not online");
            return;
        }

        ctx.result(new Gson().toJson(player));
        ctx.status(HttpStatus.OK);
    }

    public void set(@NotNull Context context) {
        StreamlinePlayer player;
        UUID uuid;

        try {
            player = new Gson().fromJson(context.body(), StreamlinePlayer.class);
            uuid = UUID.fromString(context.pathParam("uuid"));
        } catch (Exception e) {
            context.status(HttpStatus.BAD_REQUEST);
            return;
        }

        PlayerRegister.getInstance().set(uuid, player);
    }

    public void action(@NotNull Context ctx) {
        StreamlinePlayer player = PlayerRegister.getInstance().get(UUID.fromString(ctx.pathParam("uuid")));
        SocketMessage.SocketMessageType type;

        if (player == null) {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.result("Player not online");
            return;
        }

        try {
            type = SocketMessage.SocketMessageType.valueOf(ctx.pathParam("type"));
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.result("Enum not valid");
            return;
        }

        if (type.equals(SocketMessage.SocketMessageType.PLAYER_CONNECT)) {
            if (RunningServerManager.getInstance().getServerByUuid(ctx.body()) == null) {
                ctx.status(HttpStatus.BAD_REQUEST);
                ctx.result("Server not online");
            }
        }

        RunningServerManager.getInstance().getServerByUuid(player.getCurrentServerId()).send(new SocketMessage(type, ctx.body()).setPlayer(player));
    }

    public void delete(@NotNull Context context) {
        try {
            PlayerRegister.getInstance().delete(UUID.fromString(context.pathParam("uuid")));
        } catch (IllegalArgumentException e) {
            context.status(HttpStatus.BAD_REQUEST);
            return;
        }

        context.status(HttpStatus.OK);
    }
}
