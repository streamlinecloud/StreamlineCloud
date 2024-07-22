package io.streamlinemc.main.core.backend.remoteLogic;

import io.streamlinemc.api.plmanager.event.predefined.ExecuteCommandEvent;
import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.terminal.CloudTerminalRunner;
import io.streamlinemc.main.utils.Cache;
import lombok.Getter;
import lombok.SneakyThrows;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Arrays;

import static io.streamlinemc.main.plugin.PluginManager.eventManager;


public class WSClient {

    @Getter
    WebSocketClient client;

    @SneakyThrows
    public WSClient() {
        String serverURI = Cache.i().getConfig().getWebsocketUrl();
        client = new WebSocketClient(new URI(serverURI)) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                log("Connected to Websocket §YELLOW" + serverURI + ".");
                client.send("CONNECT");
                client.send("SUBSCRIBE ADD streamline/input");
            }

            @Override
            public void onMessage(String s) {

                String[] args = s.split(";");

                if (args.length < 2) return;

                String name = args[0];
                String cmd = args[1];
                log("§YELLOW " + name + ": §RED" + cmd);

                ExecuteCommandEvent executeCommandEvent = eventManager.callEvent(new ExecuteCommandEvent(cmd.split(" ")[0], Arrays.stream(cmd.split(" ")).skip(1).toArray(String[]::new), name));

                if (executeCommandEvent.isCancelled()) {
                    return;
                }

                CloudTerminalRunner.executeCommand(cmd.split(" "));

            }

            @Override
            public void onClose(int i, String s, boolean b) {
                log("Closed connection from Websocket §YELLOW" + serverURI + ", " + s + ";" + i + ".");
            }

            @Override
            public void onError(Exception e) {
                log("Error occurred on Websocket §YELLOW" + serverURI + "-> " + e.getMessage() +  ".");

            }
        };

        client.connect();

    }
    public static void log(String msg) {
        StreamlineCloud.log("§YELLOWWS-Bridge §8-> §RED" + msg);
    }


}
