package net.streamlinecloud.api.extension.event;

import net.streamlinecloud.api.exception.ListenerException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class EventManager {

    private final List<EventListener> listeners = new ArrayList<>();

    /**
     * Register a listener
     *
     * @param listener Listener to register
     */
    public void registerListener(EventListener listener) {
        listeners.add(listener);
    }

    /**
     * Unregister a listener
     *
     * @param listener Listener to unregister
     */
    public void unregisterListener(EventListener listener) {
        listeners.remove(listener);
    }

    public <T extends Event> T callEvent(T event) {
        for (EventListener listener : listeners) {
            for (Method method : listener.getClass().getMethods()) {
                if (method.isAnnotationPresent(StreamlineEvent.class)) {
                    if (method.getParameterCount() == 1 && method.getParameterTypes()[0].isAssignableFrom(event.getClass())) {
                        try {
                            method.invoke(listener, event);
                            if (event.isCancelled()) {
                                return event; // Return early if event is cancelled
                            }
                        } catch (InvocationTargetException | IllegalAccessException e) {
                            throw new ListenerException(e.getMessage(), new Throwable("Error in listener " + listener.getClass().getName() + " method " + method.getName()));
                        }
                    }
                }
            }
        }
        return event;
    }


}
