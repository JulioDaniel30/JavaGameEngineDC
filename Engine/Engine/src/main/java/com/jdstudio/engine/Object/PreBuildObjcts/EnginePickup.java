package com.jdstudio.engine.Object.PreBuildObjcts;

import org.json.JSONObject;

import com.jdstudio.engine.Components.InteractionComponent;
import com.jdstudio.engine.Components.InteractionZone;
import com.jdstudio.engine.Core.ISavable;
import com.jdstudio.engine.Graphics.Sprite.Animations.Animator;
import com.jdstudio.engine.Object.GameObject;
import com.jdstudio.engine.Utils.PropertiesReader;

/**
 * An abstract base class for any type of collectible item in the game.
 * It contains all the logic for collection, animation, and inventory integration.
 * Game-specific subclasses are responsible for providing the specific animations and defining the item.
 * This class also implements {@link ISavable} to allow its state to be persisted.
 * 
 * @author JDStudio
 */
public abstract class EnginePickup extends GameObject implements ISavable {

    protected boolean isCollected = false;
    protected Animator animator;
    protected String itemId;
    protected int quantity = 1;
    protected boolean autoPickup = false;
    protected boolean respawns = false;
    protected int respawnTime = 0; // In ticks
    protected int currentRespawnTimer = 0;
    protected boolean requiresKey = false;
    protected String requiredKeyId;

    /**
     * Constructs a new EnginePickup with the given properties.
     * 
     * @param properties A JSONObject containing the initial properties of the pickup.
     */
    public EnginePickup(JSONObject properties) {
        super(properties);
    }

    /**
     * Initializes the EnginePickup's properties from a JSONObject.
     * It sets up item ID, quantity, auto-pickup, respawn behavior, and key requirements.
     *
     * @param properties A JSONObject containing the properties to initialize.
     */
    @Override
    public void initialize(JSONObject properties) {
        super.initialize(properties);
        PropertiesReader reader = new PropertiesReader(properties);
        
        this.itemId = reader.getString("itemId", "");
        this.quantity = reader.getInt("quantity", 1);
        this.autoPickup = reader.getBoolean("autoPickup", false);
        this.respawns = reader.getBoolean("respawns", false);
        this.respawnTime = reader.getInt("respawnTime", 300); // 5 seconds at 60 FPS
        this.requiresKey = reader.getBoolean("requiresKey", false);
        this.requiredKeyId = reader.getString("requiredKeyId", "");
        this.isCollected = reader.getBoolean("isCollected", false);

        this.animator = new Animator();
        this.addComponent(animator);
        
        // Call the abstract method that the GAME class will implement
        setupAnimations(this.animator);

        // If not auto pickup, add interaction zone
        if (!autoPickup) {
            InteractionComponent interaction = new InteractionComponent();
            interaction.addZone(new InteractionZone(this, InteractionZone.TYPE_DIALOGUE, 24.0));
            this.addComponent(interaction);
        }

        updateStateVisuals();
        setCollisionType(autoPickup ? CollisionType.TRIGGER : CollisionType.SOLID);
    }
    
    /**
     * Main interaction method for manual pickup.
     * It checks for key requirements and then collects the item.
     */
    public void interact() {
        if (isCollected) return;
        if (!animator.getCurrentAnimationKey().startsWith("idle")) return;

        // Check if key is required
        if (requiresKey && !hasRequiredKey()) {
            onKeyRequired();
            return;
        }

        collectItem();
    }

    /**
     * Method for automatic collection (called by collision).
     * 
     * @param other The GameObject that collided with this pickup.
     */
    @Override
    public void onCollision(GameObject other) {
        if (!autoPickup || isCollected) return;
        
        // Check if it's the player colliding
        if (isPlayer(other)) {
            // Check if key is required
            if (requiresKey && !hasRequiredKey()) {
                onKeyRequired();
                return;
            }
            
            collectItem();
        }
    }

    /**
     * Executes the item collection process.
     * It sets the collected flag, plays the collecting animation, gives the item to the player,
     * and handles respawn logic.
     */
    protected void collectItem() {
        if (isCollected) return;
        
        isCollected = true;
        animator.play("collecting");
        
        // Call the abstract method to add the item to the player's inventory
        giveItemToPlayer(itemId, quantity);
        
        // Notify that the item was collected
        onItemCollected();
        
        // If it doesn't respawn, mark for destruction
        if (!respawns) {
            setCollisionType(CollisionType.NO_COLLISION);
        } else {
            currentRespawnTimer = respawnTime;
        }
    }

    /**
     * Updates the pickup's logic, primarily managing respawn timers and animation state transitions.
     */
    @Override
    public void tick() {
        super.tick();
        
        // Manage respawn
        if (isCollected && respawns && currentRespawnTimer > 0) {
            currentRespawnTimer--;
            if (currentRespawnTimer <= 0) {
                respawnItem();
            }
        }
        
        // Manage animations
        if (animator.getCurrentAnimation() != null && animator.getCurrentAnimation().hasFinished()) {
            String currentKey = animator.getCurrentAnimationKey();
            
            if ("collecting".equals(currentKey)) {
                if (respawns) {
                    animator.play("collected");
                } else {
                    // Item does not respawn, can be destroyed
                    this.isDestroyed = true;
                }
            } else if ("respawning".equals(currentKey)) {
                animator.play("idleAvailable");
            }
        }
    }
    
    /**
     * Respawns the item, making it available for collection again.
     * It resets the collected flag, plays the respawning animation, and updates collision type.
     */
    protected void respawnItem() {
        isCollected = false;
        animator.play("respawning");
        setCollisionType(autoPickup ? CollisionType.TRIGGER : CollisionType.SOLID);
        onItemRespawned();
    }
    
    /**
     * Updates the visual state of the pickup based on its collected status.
     * It plays different idle animations (e.g., idleAvailable, collected).
     */
    private void updateStateVisuals() {
        if (isCollected) {
            if (respawns) {
                animator.play("collected");
            } else {
                // Item collected and does not respawn - can be invisible
                animator.play("collected");
            }
        } else {
            animator.play("idleAvailable");
        }
    }

    /**
     * Saves the current state of the pickup to a JSONObject.
     * 
     * @return A JSONObject containing the pickup's savable state.
     */
    @Override
    public JSONObject saveState() {
        JSONObject state = new JSONObject();
        state.put("name", this.name);
        state.put("isCollected", this.isCollected);
        state.put("currentRespawnTimer", this.currentRespawnTimer);
        return state;
    }

    /**
     * Loads the state of the pickup from a JSONObject.
     * 
     * @param state The JSONObject containing the saved data.
     */
    @Override
    public void loadState(JSONObject state) {
        this.isCollected = state.getBoolean("isCollected");
        this.currentRespawnTimer = state.getInt("currentRespawnTimer");
        updateStateVisuals();
        
        // Update collision type based on state
        if (isCollected && !respawns) {
            setCollisionType(CollisionType.NO_COLLISION);
        } else {
            setCollisionType(autoPickup ? CollisionType.TRIGGER : CollisionType.SOLID);
        }
    }
    
    // Getters
    public boolean isCollected() { return isCollected; }
    public String getItemId() { return itemId; }
    public int getQuantity() { return quantity; }
    public boolean isAutoPickup() { return autoPickup; }
    public boolean respawns() { return respawns; }
    public int getRespawnTime() { return respawnTime; }
    public int getCurrentRespawnTimer() { return currentRespawnTimer; }
    public boolean requiresKey() { return requiresKey; }
    public String getRequiredKeyId() { return requiredKeyId; }
    
    /**
     * ABSTRACT METHODS: The game class MUST implement these methods
     */
    
    /**
     * Configures the specific animations for the pickup.
     * 
     * @param animator The Animator component to be configured.
     */
    protected abstract void setupAnimations(Animator animator);
    
    /**
     * Checks if the player possesses the required key to collect this item.
     * 
     * @return true if the key is held or if no key is needed.
     */
    protected abstract boolean hasRequiredKey();
    
    /**
     * Checks if the given GameObject is the player.
     * 
     * @param gameObject The GameObject to check.
     * @return true if it is the player, false otherwise.
     */
    protected abstract boolean isPlayer(GameObject gameObject);
    
    /**
     * Adds the item to the player's inventory.
     * 
     * @param itemId The ID of the item to be added.
     * @param quantity The quantity of the item.
     */
    protected abstract void giveItemToPlayer(String itemId, int quantity);
    
    /**
     * OPTIONAL METHODS: The game class can override these for custom behavior
     */
    
    /**
     * Called when the item is collected.
     */
    protected void onItemCollected() {
        // Default empty implementation - can be overridden
    }
    
    /**
     * Called when the item respawns.
     */
    protected void onItemRespawned() {
        // Default empty implementation - can be overridden
    }
    
    /**
     * Called when the player attempts to collect an item that requires a key without having it.
     */
    protected void onKeyRequired() {
        // Default empty implementation - can be overridden
    }
}