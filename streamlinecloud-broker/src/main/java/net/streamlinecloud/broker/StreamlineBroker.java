package net.streamlinecloud.broker;

import net.streamlinecloud.api.server.StreamlineServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StreamlineBroker {

    public static void main(String[] args) {
        SpringApplication.run(StreamlineBroker.class, args);
    }

}
