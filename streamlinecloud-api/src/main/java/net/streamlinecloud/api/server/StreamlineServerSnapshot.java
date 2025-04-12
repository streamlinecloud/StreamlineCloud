package net.streamlinecloud.api.server;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StreamlineServerSnapshot {

    String name;
    String uuid;
    int port;
    int onlineCount;
    int maxOnlineCount;

}
