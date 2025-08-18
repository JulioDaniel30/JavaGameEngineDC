// engine
package com.JDStudio.Engine.Events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {

    private static final EventManager instance = new EventManager();
    
    private final Map<Enum<?>, List<EventListener>> listeners = new HashMap<>();
    
    // --- ADIÇÃO AQUI ---
    // Uma nova lista para listeners que querem ouvir TODOS os eventos.
    private final List<EventListener> globalListeners = new ArrayList<>();

    private EventManager() {}

    public static EventManager getInstance() {
        return instance;
    }
    
    /**
     * **NOVO MÉTODO**
     * Inscreve um ouvinte que será notificado de CADA evento disparado no jogo.
     * @param listener O ouvinte a ser adicionado.
     */
    public void subscribeToAll(EventListener listener) {
        if (!globalListeners.contains(listener)) {
            globalListeners.add(listener);
        }
    }

    public void subscribe(Enum<?> eventType, EventListener listener) {
        this.listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    public void unsubscribe(Enum<?> eventType, EventListener listener) {
        if (this.listeners.containsKey(eventType)) {
            this.listeners.get(eventType).remove(listener);
        }
        // Garante que também é removido dos ouvintes globais
        globalListeners.remove(listener);
    }

    /**
     * Dispara um evento, notificando todos os ouvintes inscritos (específicos e globais).
     */
    public void trigger(Enum<?> eventType, Object data) {
        // Notifica os listeners específicos (lógica antiga)
        if (this.listeners.containsKey(eventType)) {
            new ArrayList<>(this.listeners.get(eventType)).forEach(listener -> listener.onEvent(data));
        }

        // --- ADIÇÃO AQUI ---
        // Notifica também todos os listeners globais, passando o próprio enum do evento como dado.
        new ArrayList<>(globalListeners).forEach(listener -> listener.onEvent(eventType));
    }
    
    public void reset() {
        listeners.clear();
        globalListeners.clear(); // Limpa também os ouvintes globais
        System.out.println("EventManager resetado.");
    }
}