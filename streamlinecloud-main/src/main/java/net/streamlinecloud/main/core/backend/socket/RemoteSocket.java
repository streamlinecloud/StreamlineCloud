package net.streamlinecloud.main.core.backend.socket;

import net.streamlinecloud.api.plugin.event.predefined.ExecuteCommandEvent;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.terminal.CloudTerminalRunner;
import net.streamlinecloud.main.utils.Cache;
import lombok.Getter;
import lombok.SneakyThrows;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Arrays;

import static net.streamlinecloud.main.plugin.PluginManager.eventManager;


public class RemoteSocket {

    @Getter
    WebSocketClient client;

    @SneakyThrows
    public RemoteSocket() {
        String serverURI = Cache.i().getConfig().getWebsocket().getWebsocketUrl();
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
