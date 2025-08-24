package com.jdstudio.engine.Dialogue;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single node or "page" in a conversation.
 * It contains a unique ID, the name of the speaker, the dialogue text,
 * and a list of choices available from this node.
 * 
 * @author JDStudio
 */
public class DialogueNode {
    
    /** The unique identifier for this node. */
    public final int id;
    
    /** The name of the character speaking at this node. */
    public final String speakerName;
    
    /** The line of dialogue text for this node. */
    public final String text;
    
    /** The list of choices available to the player from this node. */
    private final List<DialogueChoice> choices;

    /**
     * Constructs a new DialogueNode.
     *
     * @param id          The unique ID for the node.
     * @param speakerName The name of the speaker.
     * @param text        The dialogue text.
     */
    public DialogueNode(int id, String speakerName, String text) {
        this.id = id;
        this.speakerName = speakerName;
        this.text = text;
        this.choices = new ArrayList<>();
    }

    /**
     * Adds a choice to this node.
     *
     * @param choiceText The text to display for the choice.
     * @param nextNodeId The ID of the node to go to if this choice is selected.
     * @param action     The key of an optional action to trigger.
     * @param condition  The key of an optional condition for the choice to be visible.
     */
    public void addChoice(String choiceText, int nextNodeId, String action, String condition) {
        this.choices.add(new DialogueChoice(choiceText, nextNodeId, action, condition));
    }

    /**
     * Gets the list of choices available from this node.
     *
     * @return A list of DialogueChoice objects.
     */
    public List<DialogueChoice> getChoices() {
        return this.choices;
    }
}
