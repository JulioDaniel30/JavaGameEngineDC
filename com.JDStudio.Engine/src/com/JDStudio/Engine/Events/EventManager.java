// engine
package com.JDStudio.Engine.Events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {

    private static final EventManager instance = new EventManager();
    
    // --- MUDANÇA AQUI: O mapa agora usa um Enum como chave ---
    // Usamos 'Enum<?>' para que o manager seja genérico e possa aceitar qualquer tipo de enum.
    private final Map<Enum<?>, List<EventListener>> listeners = new HashMap<>();

    private EventManager() {}

    public static EventManager getInstance() {
        return instance;
    }

    /**
     * Inscreve um ouvinte para um tipo de evento específico.
     * @param eventType O enum que representa o evento (ex: GameEvent.ENEMY_DIED).
     * @param listener A ação a ser executada quando o evento ocorrer.
     */
    public void subscribe(Enum<?> eventType, EventListener listener) {
        this.listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    /**
     * Desinscreve um ouvinte de um tipo de evento.
     * @param eventType O enum do evento.
     * @param listener O ouvinte a ser removido.
     */
    public void unsubscribe(Enum<?> eventType, EventListener listener) {
        if (this.listeners.containsKey(eventType)) {
            this.listeners.get(eventType).remove(listener);
        }
    }

    /**
     * Dispara um evento, notificando todos os ouvintes inscritos.
     * @param eventType O enum do evento a ser disparado.
     * @param data Os dados a serem passados para os ouvintes (pode ser 'null').
     */
    public void trigger(Enum<?> eventType, Object data) {
        if (!this.listeners.containsKey(eventType)) {
            return;
        }
        new ArrayList<>(this.listeners.get(eventType)).forEach(listener -> listener.onEvent(data));
    }
    /**
     * Limpa todos os ouvintes de todos os eventos.
     * Essencial para reiniciar o estado do jogo.
     */
    public void reset() {
        listeners.clear();
        System.out.println("EventManager resetado.");
    }
}