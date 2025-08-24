package com.jdstudio.engine.Dialogue;

import com.jdstudio.engine.Events.EngineEvent;
import com.jdstudio.engine.Events.EventManager;
import com.jdstudio.engine.Object.GameObject;

/**
 * Manages the state and flow of an active conversation.
 * Acts as a Singleton to be globally accessible.
 * 
 * @author JDStudio
 */
public class DialogueManager {

    private static final DialogueManager instance = new DialogueManager();

    private Dialogue currentDialogue;
    private DialogueNode currentNode;
    private boolean isActive = false;
    
    /** The GameObject that owns the dialogue (e.g., the NPC). */
    private GameObject dialogueSource;
    /** The GameObject that is interacting (e.g., the Player). */
    private GameObject interactor;
    
    /** Timestamp for when the last dialogue ended, used for cooldown. */
    private long dialogueEndTime = 0;
    /** Cooldown period in milliseconds to prevent starting a new dialogue immediately. */
    private static long DIALOGUE_COOLDOWN = 2000; // 2 seconds cooldown

    private DialogueManager() {}

    /**
     * Gets the single instance of the DialogueManager.
     * @return The singleton instance.
     */
    public static DialogueManager getInstance() {
        return instance;
    }

    /**
     * Starts a new conversation.
     * 
     * @param dialogue   The Dialogue object to start.
     * @param source     The GameObject that is the source of the dialogue (the NPC).
     * @param interactor The GameObject that initiated the interaction (the Player).
     */
    public void startDialogue(Dialogue dialogue, GameObject source, GameObject interactor) {
    	long currentTime = System.currentTimeMillis();
        if (currentTime - dialogueEndTime < DIALOGUE_COOLDOWN) {
            System.out.println("Still in cooldown, dialogue not started.");
            return; // Exit if the cooldown is active
        }
        this.currentDialogue = dialogue;
        this.currentNode = dialogue.getStartNode(interactor);
        this.isActive = true;
        this.dialogueSource = source;
        this.interactor = interactor;
        
        // Trigger the event that a dialogue has started
        EventManager.getInstance().trigger(EngineEvent.DIALOGUE_STARTED, null);
    }

    /**
     * Ends the current conversation and triggers the DIALOGUE_ENDED event.
     */
    public void endDialogue() {
    	if (!isActive) return;
        this.currentDialogue = null;
        this.currentNode = null;
        this.isActive = false;
        this.dialogueEndTime = System.currentTimeMillis(); // Record the end time
        System.out.println("Dialogue ended.");
        // Trigger the event that the dialogue has ended
        EventManager.getInstance().trigger(EngineEvent.DIALOGUE_ENDED, null);
    }

    /**
     * Processes the player's choice and advances to the next dialogue node.
     * It also executes any action associated with the choice.
     * 
     * @param choiceIndex The index of the option chosen by the player (0, 1, 2...).
     */
    public void selectChoice(int choiceIndex) {
        if (!isActive || currentNode == null || currentNode.getChoices().isEmpty()) {
            endDialogue();
            return;
        }

        if (choiceIndex >= 0 && choiceIndex < currentNode.getChoices().size()) {
        	DialogueChoice choice = currentNode.getChoices().get(choiceIndex);
        	
            // Execute any associated action
            if (choice.action != null && !choice.action.isEmpty()) {
            	ActionManager.getInstance().executeAction(choice.action, this.interactor, this.dialogueSource);
            }
        	
            int nextNodeId = choice.nextNodeId;

            if (nextNodeId == -1) {
                endDialogue();
            } else {
                currentNode = currentDialogue.getNode(nextNodeId);
                if (currentNode == null) {
                    System.err.println("Dialogue Error: Node with id " + nextNodeId + " not found. Ending dialogue.");
                    endDialogue();
                } else {
                    System.out.println("Advancing to node #" + currentNode.id);
                }
            }
        }
    }
    
    // --- Getters and Setters ---

    /**
     * Checks if a dialogue is currently active.
     * @return true if a dialogue is active, false otherwise.
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Gets the current node of the active dialogue.
     * @return The current DialogueNode, or null if no dialogue is active.
     */
    public DialogueNode getCurrentNode() {
        return currentNode;
    }

    /**
     * Resets the DialogueManager to its initial state.
     */
    public void reset() {
        endDialogue();
        System.out.println("DialogueManager reset.");
    }

    /**
     * Gets the timestamp of when the last dialogue ended.
     * @return The end time in milliseconds.
     */
    public long getDialogueEndTime() {
        return dialogueEndTime;
    }

    /**
     * Gets the current dialogue cooldown period.
     * @return The cooldown in milliseconds.
     */
    public long getDialogueCooldown() {
        return DIALOGUE_COOLDOWN;
    }

    /**
     * Sets the dialogue cooldown period.
     * @param cooldown The new cooldown in milliseconds.
     */
    public void setDialogueCooldown(long cooldown) {
        DIALOGUE_COOLDOWN = cooldown;
    }
}
