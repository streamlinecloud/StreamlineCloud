package net.streamlinecloud.main.core.backend.controller;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import net.streamlinecloud.api.player.StreamlinePlayer;
import net.streamlinecloud.main.utils.PlayerRegister;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerController {

    public void get(@NotNull Context ctx) {
        //TODO: Check if online
        ctx.result(new Gson().toJson(PlayerRegister.getInstance().get(UUID.fromString(ctx.pathParam("uuid")))));
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

    public void action(@NotNull Context context) {
        //TODO:
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
