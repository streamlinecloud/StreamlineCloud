package io.streamlinemc.api.plmanager.event;

import lombok.Getter;
import lombok.Setter;

public abstract class Event {
    @Getter @Setter
    private boolean cancelled = false;
}
