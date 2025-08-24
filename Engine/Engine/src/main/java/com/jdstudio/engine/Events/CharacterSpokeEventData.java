package com.jdstudio.engine.Events;

import com.jdstudio.engine.Object.GameObject;

/**
 * Contains the data for the {@code EngineEvent.CHARACTER_SPOKE} event.
 * This event is triggered when a character speaks, typically in a dialogue or narration.
 * 
 * @param speaker           The GameObject that is speaking.
 * @param message           The text message spoken by the character.
 * @param durationInSeconds The duration for which the message should be displayed, in seconds.
 * @author JDStudio
 */
public record CharacterSpokeEventData(GameObject speaker, String message, float durationInSeconds) {}
