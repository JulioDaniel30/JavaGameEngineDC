package com.jdstudio.engine.Object;

import java.awt.Graphics;

import org.json.JSONObject;

import com.jdstudio.engine.Components.InteractionComponent;
import com.jdstudio.engine.Components.InteractionZone;
import com.jdstudio.engine.Dialogue.ConditionManager;
import com.jdstudio.engine.Dialogue.Dialogue;
import com.jdstudio.engine.Dialogue.DialogueManager;
import com.jdstudio.engine.Dialogue.DialogueNode;
import com.jdstudio.engine.Dialogue.DialogueParser;
import com.jdstudio.engine.Utils.PropertiesReader;

/**
 * A specialization of {@link GameObject} that represents an object capable of initiating dialogue.
 * It automatically sets up an {@link InteractionComponent} for dialogue triggers
 * and loads dialogue data from a specified JSON file.
 * 
 * @author JDStudio
 */
public class DialogableGameObject extends GameObject{

	/** The Dialogue object associated with this GameObject. */
	protected Dialogue dialogue;
	/** The path to the JSON file containing the dialogue data. */
	protected String dialoguePath;
    /** The radius within which interaction for dialogue can occur. */
    protected int interactionRadius;
	
	/**
	 * Constructs a new DialogableGameObject.
	 * 
	 * @param properties A JSONObject containing the initial properties of the dialogable object.
	 */
	public DialogableGameObject(JSONObject properties) {
		super(properties);
	}
	
	/**
	 * Initializes the DialogableGameObject's properties.
	 * It sets up an {@link InteractionComponent} with a dialogue zone and loads the dialogue from a JSON file.
	 *
	 * @param properties A JSONObject containing the properties to initialize.
	 */
	@Override
	public void initialize(JSONObject properties) {
		super.initialize(properties);
        PropertiesReader reader = new PropertiesReader(properties);
        this.interactionRadius = reader.getInt("interactionRadius", 24);
        
        InteractionComponent interaction = new InteractionComponent();

        // Create a circular interaction zone with TYPE_DIALOGUE and the specified radius
        interaction.addZone(new InteractionZone(this, InteractionZone.TYPE_DIALOGUE, interactionRadius));

        // Add the component to the GameObject
        this.addComponent(interaction);
           
       
        dialoguePath = reader.getString("dialogueFile", null);
        if (dialoguePath != null && !dialoguePath.isEmpty()) {
            System.out.println("NPC '" + this.name + "' attempting to load dialogue from: " + dialoguePath);
            this.dialogue = DialogueParser.parseDialogue(dialoguePath);
            if (this.dialogue == null) {
                System.err.println("ERROR: Dialogue for NPC '" + this.name + "' failed to load.");
            }
        } else {
            System.err.println("WARNING: NPC '" + this.name + "' does not have 'dialogueFile' property defined in Tiled.");
        }
	}
	
	/**
	 * Gets the Dialogue object associated with this GameObject.
	 * 
	 * @return The Dialogue object.
	 */
    public Dialogue getDialogue() {
        return this.dialogue;
    }
    
    /**
     * Starts a dialogue with the interactor, filtering choices based on conditions.
     * It parses a fresh copy of the dialogue and removes choices whose conditions are not met.
     * 
     * @param interactor The GameObject that is interacting (e.g., the player).
     */
    public void startFilteredDialogue(GameObject interactor) {
        // Create a fresh copy of the dialogue to avoid modifying the original
        Dialogue filteredDialogue = DialogueParser.parseDialogue(this.dialoguePath); // Assuming the NPC has its dialogue path
        if (filteredDialogue == null) return;
        
        // Iterate through all dialogue nodes
        for (DialogueNode node : filteredDialogue.getNodes().values()) {
            node.getChoices().removeIf(choice -> {
                String condition = choice.getCondition();
                if (condition == null) {
                    return false; // No condition, the choice is never removed
                }

                // The logic is now delegated to the ConditionManager
                // The engine asks the game if the condition is met.
                boolean conditionMet = ConditionManager.getInstance().checkCondition(condition, interactor);
                
                // The choice will be removed if the condition is NOT met
                return !conditionMet;
            });
        }

        // Start the dialogue with the filtered choices
        DialogueManager.getInstance().startDialogue(filteredDialogue, this, interactor);
    }
    
    /**
     * Renders debug information for the DialogableGameObject.
     * Currently, it just calls the superclass's renderDebug method.
     *
     * @param g The Graphics context to draw on.
     */
    @Override
    public void renderDebug(Graphics g) {
    	super.renderDebug(g);
    }

}