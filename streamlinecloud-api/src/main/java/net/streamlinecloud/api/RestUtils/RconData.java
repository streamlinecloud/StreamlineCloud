package io.streamlinemc.api.RestUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class RconData {

    String ip;
    int port;
    String password;

}
