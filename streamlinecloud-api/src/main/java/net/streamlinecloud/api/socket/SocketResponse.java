package net.streamlinecloud.api.socket;

import lombok.*;

import java.time.Instant;

@NoArgsConstructor
@RequiredArgsConstructor
@Getter @Setter
public class SocketResponse {

    @NonNull
    Object content;

    @NonNull
    String sender;

    @NonNull
    SocketResponseType responseType;

}
