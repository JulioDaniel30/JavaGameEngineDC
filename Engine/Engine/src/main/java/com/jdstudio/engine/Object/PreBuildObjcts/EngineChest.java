package com.jdstudio.engine.Object.PreBuildObjcts;

import org.json.JSONObject;

import com.jdstudio.engine.Components.InteractionComponent;
import com.jdstudio.engine.Components.InteractionZone;
import com.jdstudio.engine.Core.ISavable;
import com.jdstudio.engine.Graphics.Sprite.Animations.Animator;
import com.jdstudio.engine.Object.GameObject;
import com.jdstudio.engine.Utils.PropertiesReader;

/**
 * An abstract base class for any type of chest or container in the game.
 * It handles the state logic (open/closed, looted), animation, interaction, and loot system.
 * Game-specific subclasses are responsible for providing the specific animations and defining the loot.
 * This class also implements {@link ISavable} to allow its state to be persisted.
 * 
 * @author JDStudio
 */
public abstract class EngineChest extends GameObject implements ISavable {

    protected boolean isOpen = false;
    protected boolean hasBeenLooted = false;
    protected Animator animator;
    protected String lootTable;
    protected boolean requiresKey = false;
    protected String requiredKeyId;

    /**
     * Constructs a new EngineChest with the given properties.
     * 
     * @param properties A JSONObject containing the initial properties of the chest.
     */
    public EngineChest(JSONObject properties) {
        super(properties);
    }

    /**
     * Initializes the EngineChest's properties from a JSONObject.
     * It sets up the open/looted state, loot table, key requirements, and animator.
     *
     * @param properties A JSONObject containing the properties to initialize.
     */
    @Override
    public void initialize(JSONObject properties) {
        super.initialize(properties);
        PropertiesReader reader = new PropertiesReader(properties);
        
        this.isOpen = reader.getBoolean("startsOpen", false);
        this.hasBeenLooted = reader.getBoolean("hasBeenLooted", false);
        this.lootTable = reader.getString("lootTable", "");
        this.requiresKey = reader.getBoolean("requiresKey", false);
        this.requiredKeyId = reader.getString("requiredKeyId", "");

        this.animator = new Animator();
        this.addComponent(animator);
        
        // Call the abstract method that the GAME class will implement
        setupAnimations(this.animator);

        // Add manual interaction zone
        InteractionComponent interaction = new InteractionComponent();
        interaction.addZone(new InteractionZone(this, InteractionZone.TYPE_DIALOGUE, 24.0));
        this.addComponent(interaction);

        updateStateVisuals();
        setCollisionType(CollisionType.SOLID);
    }
    
    /**
     * Main interaction method with the chest.
     * It handles opening the chest (with optional key requirement) and looting it.
     */
    public void interact() {
        if (!animator.getCurrentAnimationKey().startsWith("idle")) return;

        // If already open and looted, do nothing
        if (isOpen && hasBeenLooted) {
            onAlreadyLooted();
            return;
        }

        // If closed, try to open
        if (!isOpen) {
            if (requiresKey && !hasRequiredKey()) {
                onKeyRequired();
                return;
            }
            
            // Open the chest
            animator.play("opening");
            isOpen = true;
            onChestOpened();
        }
        
        // If open but not looted, allow looting
        if (isOpen && !hasBeenLooted) {
            lootChest();
        }
    }

    /**
     * Executes the looting process of the chest.
     * It sets the {@code hasBeenLooted} flag and calls the game-specific loot giving method.
     */
    protected void lootChest() {
        if (hasBeenLooted) return;
        
        hasBeenLooted = true;
        
        // Call the abstract method to give game-specific loot
        giveLoot(lootTable);
        
        // Notify that the chest was looted
        onChestLooted();
    }

    /**
     * Updates the chest's logic, primarily handling animation state transitions.
     */
    @Override
    public void tick() {
        super.tick();
        
        if (animator.getCurrentAnimation() != null && animator.getCurrentAnimation().hasFinished()) {
            if ("opening".equals(animator.getCurrentAnimationKey())) {
                if (hasBeenLooted) {
                    animator.play("idleOpenEmpty");
                } else {
                    animator.play("idleOpenFull");
                }
            }
        }
    }
    
    /**
     * Updates the visual state of the chest based on its open/looted status.
     * It plays different idle animations (e.g., idleClosed, idleOpenFull, idleOpenEmpty).
     */
    private void updateStateVisuals() {
        if (isOpen) {
            if (hasBeenLooted) {
                animator.play("idleOpenEmpty");
            } else {
                animator.play("idleOpenFull");
            }
        } else {
            animator.play("idleClosed");
        }
    }

    /**
     * Saves the current state of the chest to a JSONObject.
     * 
     * @return A JSONObject containing the chest's savable state.
     */
    @Override
    public JSONObject saveState() {
        JSONObject state = new JSONObject();
        state.put("name", this.name);
        state.put("isOpen", this.isOpen);
        state.put("hasBeenLooted", this.hasBeenLooted);
        return state;
    }

    /**
     * Loads the state of the chest from a JSONObject.
     * 
     * @param state The JSONObject containing the saved data.
     */
    @Override
    public void loadState(JSONObject state) {
        this.isOpen = state.getBoolean("isOpen");
        this.hasBeenLooted = state.getBoolean("hasBeenLooted");
        updateStateVisuals();
    }
    
    // Getters
    public boolean isOpen() { return isOpen; }
    public boolean hasBeenLooted() { return hasBeenLooted; }
    public String getLootTable() { return lootTable; }
    public boolean requiresKey() { return requiresKey; }
    public String getRequiredKeyId() { return requiredKeyId; }
    
    /**
     * ABSTRACT METHODS: The game class MUST implement these methods
     */
    
    /**
     * Configures the specific animations for the chest.
     * 
     * @param animator The Animator component to be configured.
     */
    protected abstract void setupAnimations(Animator animator);
    
    /**
     * Checks if the player possesses the required key to open this chest.
     * 
     * @return true if the key is held or if no key is needed.
     */
    protected abstract boolean hasRequiredKey();
    
    /**
     * Gives the game-specific loot to the player.
     * 
     * @param lootTable The loot table to be used.
     */
    protected abstract void giveLoot(String lootTable);
    
    /**
     * OPTIONAL METHODS: The game class can override these for custom behavior
     */
    
    /**
     * Called when the chest is opened for the first time.
     */
    protected void onChestOpened() {
        // Default empty implementation - can be overridden
    }
    
    /**
     * Called when the chest is looted.
     */
    protected void onChestLooted() {
        // Default empty implementation - can be overridden
    }
    
    /**
     * Called when the player attempts to open a chest that requires a key without having it.
     */
    protected void onKeyRequired() {
        // Default empty implementation - can be overridden
    }
    
    /**
     * Called when the player interacts with an already looted chest.
     */
    protected void onAlreadyLooted() {
        // Default empty implementation - can be overridden
    }
}