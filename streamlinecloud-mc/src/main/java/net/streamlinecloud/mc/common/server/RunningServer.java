package net.streamlinecloud.mc.common.server;

import lombok.Getter;
import net.streamlinecloud.api.server.StreamlineServer;
import net.streamlinecloud.api.server.StreamlineServerSnapshot;
import net.streamlinecloud.mc.common.utils.BackendRequest;

import java.lang.reflect.Field;

@Getter
public class RunningServer extends StreamlineServer {

    public RunningServer(StreamlineServer s) {
        Class<?> clazz = StreamlineServer.class;
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(s);
                    field.set(this, value);
                } catch (Exception ignored) {
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    public boolean stop() {
        return restMethod("stop");
    }
    public boolean restart() {
        return restMethod("restart");
    }
    public boolean kill() {
        return restMethod("kill");
    }

    private boolean restMethod(String method) {
        return 200 == new BackendRequest("servers/" + getUuid() + "/" + method).setType(BackendRequest.RestType.POST).fetch().getStatusCode();
    }

}
