package net.streamlinecloud.api.node;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class StreamlineNode {

    UUID uuid;
    boolean main;

}
