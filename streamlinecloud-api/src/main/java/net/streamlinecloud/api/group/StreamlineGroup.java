package net.streamlinecloud.api.group;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.streamlinecloud.api.server.ServerRuntime;

import java.util.List;

@Getter
@Setter
public class StreamlineGroup implements StreamlineGroupImpl {

    String name;
    String javaExec;
    List<String> templates;
    ServerRuntime runtime;
    boolean staticGroup = false;
    int serverOnlineCount = 50;
    int minOnlineCount;
    int overflowMinutes;

}
