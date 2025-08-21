package com.jdstudio.engine.Events;

/**
 * Uma interface funcional que representa um "ouvinte" de eventos.
 * Ela recebe um objeto de dados genérico que pode ser usado para passar informações sobre o evento.
 */
@FunctionalInterface
public interface EventListener {
    void onEvent(Object data);
}