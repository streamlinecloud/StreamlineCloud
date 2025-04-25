package net.streamlinecloud.api.extension.event;

import lombok.Getter;
import lombok.Setter;

public abstract class Event {
    @Getter @Setter
    private boolean cancelled = false;
}
