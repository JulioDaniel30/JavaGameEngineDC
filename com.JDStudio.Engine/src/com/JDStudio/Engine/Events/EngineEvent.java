package com.JDStudio.Engine.Events;

public enum EngineEvent {
    /**
     * Disparado quando um mundo (World) termina de ser carregado e todos os seus
     * objetos foram criados. O dado do evento é um objeto WorldLoadedEventData.
     */
    WORLD_LOADED,
	CHARACTER_SPOKE,
	TARGET_ENTERED_ZONE,
    TARGET_EXITED_ZONE,
    INTERACTION_TRIGGERED;
}