package com.jdstudio.engine.Dialogue;

/**
 * Represents a single choice that a player can make in a dialogue.
 * A choice has text to display, a destination node to go to, an optional action to trigger,
 * and an optional condition that must be met for the choice to be visible.
 * 
 * @author JDStudio
 */
public class DialogueChoice {
    
    /** The text displayed to the player for this choice. */
    public final String text;
    
    /** The ID of the node to go to if this choice is selected. */
    public final int nextNodeId;
    
    /** The key of the action to execute when this choice is selected (optional). */
    public final String action;
    
    /** The key of the condition that must be met for this choice to be available (optional). */
    public final String condition;

    /**
     * Constructs a new DialogueChoice.
     *
     * @param text       The text to display for the choice.
     * @param nextNodeId The ID of the destination node.
     * @param action     The key of the action to trigger (can be null or empty).
     * @param condition  The key of the condition for visibility (can be null or empty).
     */
    public DialogueChoice(String text, int nextNodeId, String action, String condition) {
        this.text = text;
        this.nextNodeId = nextNodeId;
        this.action = action;
        this.condition = condition;
    }
    
    /**
     * Gets the condition key for this choice.
     *
     * @return The condition string, or null if there is none.
     */
    public String getCondition() {
        return this.condition;
    }
}
