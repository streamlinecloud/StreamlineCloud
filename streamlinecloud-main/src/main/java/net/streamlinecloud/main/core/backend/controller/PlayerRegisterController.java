package net.streamlinecloud.main.core.backend.controller;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import net.streamlinecloud.api.player.StreamlinePlayer;
import net.streamlinecloud.main.utils.PlayerRegister;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerRegisterController {

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
