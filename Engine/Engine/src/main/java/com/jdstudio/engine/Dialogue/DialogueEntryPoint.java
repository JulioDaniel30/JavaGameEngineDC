package com.jdstudio.engine.Dialogue;

/**
 * Represents a conditional entry point for a dialogue.
 * If the specified condition is met, the dialogue will start from the given node ID
 * instead of the default one.
 * 
 * @param condition The key of the condition to check.
 * @param nodeId    The ID of the node to start from if the condition is true.
 * @author JDStudio
 */
public record DialogueEntryPoint(String condition, int nodeId) {}
