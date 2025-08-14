package com.JDStudio.Engine.Events;

import com.JDStudio.Engine.Object.GameObject;

/**
 * Contém os dados para o evento CHARACTER_SPOKE.
 */
public record CharacterSpokeEventData(GameObject speaker, String message, float durationInSeconds) {}