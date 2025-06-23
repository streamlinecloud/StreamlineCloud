package net.streamlinecloud.main.core.backend;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BackendSession {

    final String key;
    String[] allowedRequests;
    boolean adminAccess = false;

    public BackendSession(String key, String[] allowedRequests) {
        this.key = key;
        this.allowedRequests = allowedRequests;
    }

}
