package com.jdstudio.engine.Events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A singleton manager for a publish-subscribe event system.
 * It allows different parts of the engine to communicate without being directly coupled.
 * Supports subscribing to specific event types or to all events globally.
 * 
 * @author JDStudio
 */
public class EventManager {

    private static final EventManager instance = new EventManager();
    
    /** A map where keys are event types and values are lists of listeners for that event. */
    private final Map<Enum<?>, List<EventListener>> listeners = new HashMap<>();
    
    /** A list of listeners that want to be notified of ALL events. */
    private final List<EventListener> globalListeners = new ArrayList<>();

    private EventManager() {}

    /**
     * Gets the single instance of the EventManager.
     * @return The singleton instance.
     */
    public static EventManager getInstance() {
        return instance;
    }
    
    /**
     * Subscribes a listener that will be notified of EVERY event triggered in the game.
     * The event type enum itself will be passed as the data to the listener.
     * 
     * @param listener The listener to be added.
     */
    public void subscribeToAll(EventListener listener) {
        if (!globalListeners.contains(listener)) {
            globalListeners.add(listener);
        }
    }

    /**
     * Subscribes a listener to a specific event type.
     * 
     * @param eventType The type of event to listen for (e.g., EngineEvent.DIALOGUE_STARTED).
     * @param listener  The listener that will be notified.
     */
    public void subscribe(Enum<?> eventType, EventListener listener) {
        this.listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    /**
     * Unsubscribes a listener from a specific event type and from the global list.
     * 
     * @param eventType The event type to unsubscribe from.
     * @param listener  The listener to remove.
     */
    public void unsubscribe(Enum<?> eventType, EventListener listener) {
        if (this.listeners.containsKey(eventType)) {
            this.listeners.get(eventType).remove(listener);
        }
        // Also ensure it's removed from the global listeners
        globalListeners.remove(listener);
    }

    /**
     * Triggers an event, notifying all subscribed listeners (both specific and global).
     * 
     * @param eventType The type of event being triggered.
     * @param data      The data associated with the event.
     */
    public void trigger(Enum<?> eventType, Object data) {
        // Notify specific listeners
        if (this.listeners.containsKey(eventType)) {
            // Create a copy to avoid ConcurrentModificationException if a listener unsubscribes
            new ArrayList<>(this.listeners.get(eventType)).forEach(listener -> listener.onEvent(data));
        }

        // Notify all global listeners, passing the event type enum itself as data.
        new ArrayList<>(globalListeners).forEach(listener -> listener.onEvent(eventType));
    }
    
    /**
     * Clears all registered listeners, both specific and global.
     * Useful for resetting the engine state.
     */
    public void reset() {
        listeners.clear();
        globalListeners.clear();
        System.out.println("EventManager reset.");
    }
}
