package com.jdstudio.engine.Events;

/**
 * A functional interface that represents an "event listener".
 * Implementations of this interface can subscribe to events via the EventManager
 * and will be notified when an event occurs.
 * 
 * @author JDStudio
 */
@FunctionalInterface
public interface EventListener {
    /**
     * Called when an event is triggered.
     * 
     * @param data An object containing data related to the event. The type of this object
     *             depends on the specific event that was triggered.
     */
    void onEvent(Object data);
}
