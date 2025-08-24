package com.jdstudio.engine.Components;

/**
 * A data component that holds information for an interaction prompt.
 * This is used to display a message to the player when they are near an interactable object.
 * For example: "[E] Talk", "[F] Open".
 * 
 * @author JDStudio
 */
public class InteractionPromptComponent extends Component {
    
    /** The text to be displayed in the interaction prompt (e.g., "[E] Talk"). */
    public final String promptText;

    /**
     * Constructs a new InteractionPromptComponent.
     * 
     * @param promptText The text to be displayed when the player is nearby.
     */
    public InteractionPromptComponent(String promptText) {
        this.promptText = promptText;
    }
}
