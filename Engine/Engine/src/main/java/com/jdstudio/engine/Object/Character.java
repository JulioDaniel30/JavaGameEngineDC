package com.jdstudio.engine.Object;

import org.json.JSONObject;

import com.jdstudio.engine.Events.CharacterSpokeEventData;
import com.jdstudio.engine.Events.EngineEvent;
import com.jdstudio.engine.Events.EventManager;
import com.jdstudio.engine.Graphics.Layers.RenderLayer;
import com.jdstudio.engine.Graphics.Layers.RenderManager;
import com.jdstudio.engine.Graphics.Layers.StandardLayers;
import com.jdstudio.engine.Utils.PropertiesReader;

/**
 * A specialization of {@link GameObject} that represents a "character" in the game.
 * It adds concepts such as health, damage, and death.
 * This is a base class for Players, Enemies, NPCs, etc.
 * 
 * @author JDStudio
 */
public abstract class Character extends GameObject {

	/** The current life points of the character. */
	public double life;
    /** The maximum life points of the character. */
    public double maxLife;
    /** Flag indicating if the character is dead. */
    protected boolean isDead = false;
    
    /**
     * Constructs a new Character with the given properties.
     * 
     * @param properties A JSONObject containing the initial properties of the character.
     */
    public Character(JSONObject properties) {
        super(properties);
    }

    /**
     * Initializes the Character's properties from a JSONObject.
     * Sets the default collision type to CHARACTER_TRIGGER and render layer to CHARACTERS.
     *
     * @param properties A JSONObject containing the properties to initialize.
     */
    @Override
    public void initialize(JSONObject properties) {
        super.initialize(properties);
        setCollisionType(CollisionType.CHARACTER_TRIGGER);
        PropertiesReader reader = new PropertiesReader(properties);
        String layerName = reader.getString("renderLayer", "CHARACTERS");
        
        // Ask the RenderManager to find the layer with that name.
        RenderLayer layer = RenderManager.getInstance().getLayerByName(layerName);
        
        // Set the GameObject's rendering layer.
        if (layer != null) {
            this.setRenderLayer(layer);
        } else {
            // If the layer name in Tiled is invalid or not registered,
            // use the engine's default and warn in the console.
            System.err.println("Warning: RenderLayer '" + layerName + "' invalid or not registered for object '" + this.name + "'. Using default layer.");
            this.setRenderLayer(StandardLayers.CHARACTERS);
        }
    }

    /**
     * Handles the character's death logic.
     * Sets the {@code isDead} and {@code isDestroyed} flags to true,
     * and changes the collision type to NO_COLLISION.
     */
    protected void die() {
        this.isDead = true;
        this.isDestroyed = true; 
        setCollisionType(CollisionType.NO_COLLISION);
    }
    
    /**
     * Makes this character "say" something, triggering an event for the game to react.
     * The engine only announces the event, without knowing how it will be rendered (e.g., chat bubble).
     * 
     * @param message           The message to be displayed.
     * @param durationInSeconds The duration in seconds for which the message should be displayed.
     */
    public void say(String message, float durationInSeconds) {
        EventManager.getInstance().trigger(
            EngineEvent.CHARACTER_SPOKE, 
            new CharacterSpokeEventData(this, message, durationInSeconds)
        );
    }

    /**
     * Updates the character's logic.
     * It first checks if the character is dead or destroyed, then calls the superclass's tick method.
     */
    @Override
    public void tick() {
        // First, check the character's state.
        if (isDead || isDestroyed) return;
        
        // Then, call the GameObject's tick, which will update all components.
        super.tick();
    }

    /**
     * Applies a specified amount of damage to this character.
     * If life drops to 0 or below, the character dies.
     * 
     * @param amount The amount of damage to be applied.
     */
    public void takeDamage(double amount) {
        // A character with 0 or less life cannot take more damage.
        if (this.life <= 0) return;

        this.life -= amount;
        if (this.life <= 0) {
            this.life = 0;
            die(); // Call the die() method that handles isDead/isDestroyed flags
        }
    }

    /**
     * Heals a specified amount of life to this character.
     * Life will not exceed maximum life.
     * 
     * @param amount The amount of life to be healed.
     */
    public void heal(double amount) {
        if (isDead) return;

        this.life += amount;
        if (this.life > this.maxLife) {
            this.life = this.maxLife;
        }
    }

    /**
     * Checks if the character is dead.
     * @return true if the character's life is 0 or less, false otherwise.
     */
    public boolean isDead() {
        return isDead;
    }
}
