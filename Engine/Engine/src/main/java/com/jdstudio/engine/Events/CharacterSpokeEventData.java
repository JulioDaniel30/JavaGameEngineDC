package com.jdstudio.engine.Events;

import com.jdstudio.engine.Object.GameObject;

/**
 * Contém os dados para o evento CHARACTER_SPOKE.
 */
public record CharacterSpokeEventData(GameObject speaker, String message, float durationInSeconds) {}