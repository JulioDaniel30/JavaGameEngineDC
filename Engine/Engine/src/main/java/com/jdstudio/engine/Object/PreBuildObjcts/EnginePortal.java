package com.jdstudio.engine.Object.PreBuildObjcts;

import org.json.JSONObject;

import com.jdstudio.engine.Components.InteractionComponent;
import com.jdstudio.engine.Components.InteractionZone;
import com.jdstudio.engine.Core.ISavable;
import com.jdstudio.engine.Graphics.Sprite.Animations.Animator;
import com.jdstudio.engine.Object.GameObject;
import com.jdstudio.engine.Utils.PropertiesReader;

/**
 * An abstract base class for any type of portal or teleporter in the game.
 * It contains all the logic for teleportation, animation, and activation conditions.
 * Game-specific subclasses are responsible for providing the specific animations and defining the destination.
 * This class also implements {@link ISavable} to allow its state to be persisted.
 * 
 * @author JDStudio
 */
public abstract class EnginePortal extends GameObject implements ISavable {

    protected boolean isActive = true;
    protected boolean isUsed = false;
    protected Animator animator;
    protected String portalId;
    protected String destinationLevel;
    protected int destinationX;
    protected int destinationY;
    protected String destinationPortalId;
    protected boolean requiresKey = false;
    protected String requiredKeyId;
    protected boolean requiresQuest = false;
    protected String requiredQuestId;
    protected boolean autoTeleport = false;
    protected int cooldownTime = 0; // In ticks
    protected int currentCooldown = 0;
    protected boolean oneTimeUse = false;

    /**
     * Constructs a new EnginePortal with the given properties.
     * 
     * @param properties A JSONObject containing the initial properties of the portal.
     */
    public EnginePortal(JSONObject properties) {
        super(properties);
    }

    /**
     * Initializes the EnginePortal's properties from a JSONObject.
     * It sets up activation state, destination, key/quest requirements, and auto-teleport behavior.
     * <p>
     * The following properties can be defined in the JSON object:
     * <ul>
     *   <li><b>Key:</b> "isActive"<br/><b>Type:</b> boolean<br/><b>Description:</b> If true, the portal is initially active and usable. Default is true.</li>
     *   <li><b>Key:</b> "portalId"<br/><b>Type:</b> String<br/><b>Description:</b> A unique identifier for this portal, used for linking with other portals. Default is empty string.</li>
     *   <li><b>Key:</b> "destinationLevel"<br/><b>Type:</b> String<br/><b>Description:</b> The name of the level to teleport to. Default is empty string.</li>
     *   <li><b>Key:</b> "destinationX"<br/><b>Type:</b> int<br/><b>Description:</b> The X coordinate in the destination level to teleport to. Default is 0.</li>
     *   <li><b>Key:</b> "destinationY"<br/><b>Type:</b> int<br/><b>Description:</b> The Y coordinate in the destination level to teleport to. Default is 0.</li>
     *   <li><b>Key:</b> "destinationPortalId"<br/><b>Type:</b> String<br/><b>Description:</b> The ID of a specific portal in the destination level to teleport to. If set, destinationX/Y might be ignored. Default is empty string.</li>
     *   <li><b>Key:</b> "requiresKey"<br/><b>Type:</b> boolean<br/><b>Description:</b> If true, the portal requires a specific key to be used. Default is false.</li>
     *   <li><b>Key:</b> "requiredKeyId"<br/><b>Type:</b> String<br/><b>Description:</b> The ID of the key required to use the portal, if "requiresKey" is true. Default is empty string.</li>
     *   <li><b>Key:</b> "requiresQuest"<br/><b>Type:</b> boolean<br/><b>Description:</b> If true, the portal requires a specific quest to be completed. Default is false.</li>
     *   <li><b>Key:</b> "requiredQuestId"<br/><b>Type:</b> String<br/><b>Description:</b> The ID of the quest required to use the portal, if "requiresQuest" is true. Default is empty string.</li>
     *   <li><b>Key:</b> "autoTeleport"<br/><b>Type:</b> boolean<br/><b>Description:</b> If true, the portal teleports the player automatically on collision. If false, it requires interaction. Default is false.</li>
     *   <li><b>Key:</b> "cooldownTime"<br/><b>Type:</b> int<br/><b>Description:</b> The cooldown time in ticks after the portal is used before it can be used again. Default is 60 ticks (1 second).</li>
     *   <li><b>Key:</b> "oneTimeUse"<br/><b>Type:</b> boolean<br/><b>Description:</b> If true, the portal can only be used once. Default is false.</li>
     *   <li><b>Key:</b> "isUsed"<br/><b>Type:</b> boolean<br/><b>Description:</b> Internal state: If true, the one-time use portal has already been used. Default is false.</li>
     * </ul>
     *
     * @param properties A JSONObject containing the properties to initialize.
     */
    @Override
    public void initialize(JSONObject properties) {
        super.initialize(properties);
        PropertiesReader reader = new PropertiesReader(properties);
        
        this.isActive = reader.getBoolean("isActive", true);
        this.portalId = reader.getString("portalId", "");
        this.destinationLevel = reader.getString("destinationLevel", "");
        this.destinationX = reader.getInt("destinationX", 0);
        this.destinationY = reader.getInt("destinationY", 0);
        this.destinationPortalId = reader.getString("destinationPortalId", "");
        this.requiresKey = reader.getBoolean("requiresKey", false);
        this.requiredKeyId = reader.getString("requiredKeyId", "");
        this.requiresQuest = reader.getBoolean("requiresQuest", false);
        this.requiredQuestId = reader.getString("requiredQuestId", "");
        this.autoTeleport = reader.getBoolean("autoTeleport", false);
        this.cooldownTime = reader.getInt("cooldownTime", 60); // 1 second at 60 FPS
        this.oneTimeUse = reader.getBoolean("oneTimeUse", false);
        this.isUsed = reader.getBoolean("isUsed", false);

        this.animator = new Animator();
        this.addComponent(animator);
        
        // Call the abstract method that the GAME class will implement
        setupAnimations(this.animator);

        // If not auto teleport, add interaction zone
        if (!autoTeleport) {
            InteractionComponent interaction = new InteractionComponent();
            interaction.addZone(new InteractionZone(this, InteractionZone.TYPE_DIALOGUE, 24.0));
            this.addComponent(interaction);
        }

        updateStateVisuals();
        setCollisionType(autoTeleport ? CollisionType.TRIGGER : CollisionType.SOLID);
    }
    
    /**
     * Main interaction method with the portal (for manual teleport).
     * It checks conditions and initiates teleportation if allowed.
     */
    public void interact() {
        if (!isActive || currentCooldown > 0) return;
        if (oneTimeUse && isUsed) return;
        if (!animator.getCurrentAnimationKey().startsWith("idle")) return;

        // Check usage conditions
        if (!canUsePortal()) {
            return;
        }

        teleportPlayer();
    }

    /**
     * Method for automatic teleportation (called by collision).
     * 
     * @param other The GameObject that collided with this portal.
     */
    @Override
    public void onCollision(GameObject other) {
        if (!autoTeleport || !isActive || currentCooldown > 0) return;
        if (oneTimeUse && isUsed) return;
        
        // Check if it's the player colliding
        if (isPlayer(other)) {
            // Check usage conditions
            if (!canUsePortal()) {
                return;
            }
            
            teleportPlayer();
        }
    }

    /**
     * Checks if the portal can currently be used based on its active state, cooldown, and requirements.
     * 
     * @return true if the portal can be used, false otherwise.
     */
    protected boolean canUsePortal() {
        // Check if key is required
        if (requiresKey && !hasRequiredKey()) {
            onKeyRequired();
            return false;
        }
        
        // Check if quest is required
        if (requiresQuest && !hasRequiredQuest()) {
            onQuestRequired();
            return false;
        }
        
        return true;
    }

    /**
     * Executes the teleportation process.
     * It plays the activation animation, sets cooldown, marks as used (if one-time use),
     * and calls the game-specific teleportation method.
     */
    protected void teleportPlayer() {
        if (!isActive) return;
        
        animator.play("activating");
        currentCooldown = cooldownTime;
        
        if (oneTimeUse) {
            isUsed = true;
            isActive = false;
        }
        
        // Call the abstract method to perform game-specific teleportation
        performTeleport(destinationLevel, destinationX, destinationY, destinationPortalId);
        
        // Notify that the portal was used
        onPortalUsed();
    }

    /**
     * Activates the portal, making it usable.
     */
    public void activate() {
        if (oneTimeUse && isUsed) return;
        
        isActive = true;
        updateStateVisuals();
        onPortalActivated();
    }

    /**
     * Deactivates the portal, making it unusable.
     */
    public void deactivate() {
        isActive = false;
        updateStateVisuals();
        onPortalDeactivated();
    }

    /**
     * Updates the portal's logic, primarily managing cooldown timers and animation state transitions.
     */
    @Override
    public void tick() {
        super.tick();
        
        // Manage cooldown
        if (currentCooldown > 0) {
            currentCooldown--;
        }
        
        // Manage animations
        if (animator.getCurrentAnimation() != null && animator.getCurrentAnimation().hasFinished()) {
            String currentKey = animator.getCurrentAnimationKey();
            
            if ("activating".equals(currentKey)) {
                updateStateVisuals();
            }
        }
    }
    
    /**
     * Updates the visual state of the portal based on its active state, cooldown, and usage.
     * It plays different idle animations (e.g., idleActive, idleInactive, idleCooldown).
     */
    private void updateStateVisuals() {
        if (!isActive || (oneTimeUse && isUsed)) {
            animator.play("idleInactive");
        } else if (currentCooldown > 0) {
            animator.play("idleCooldown");
        } else {
            animator.play("idleActive");
        }
    }

    /**
     * Saves the current state of the portal to a JSONObject.
     * 
     * @return A JSONObject containing the portal's savable state.
     */
    @Override
    public JSONObject saveState() {
        JSONObject state = new JSONObject();
        state.put("name", this.name);
        state.put("isActive", this.isActive);
        state.put("isUsed", this.isUsed);
        state.put("currentCooldown", this.currentCooldown);
        return state;
    }

    /**
     * Loads the state of the portal from a JSONObject.
     * 
     * @param state The JSONObject containing the saved data.
     */
    @Override
    public void loadState(JSONObject state) {
        this.isActive = state.getBoolean("isActive");
        this.isUsed = state.getBoolean("isUsed");
        this.currentCooldown = state.getInt("currentCooldown");
        updateStateVisuals();
    }
    
    // Getters
    public boolean isActive() { return isActive; }
    public boolean isUsed() { return isUsed; }
    public String getPortalId() { return portalId; }
    public String getDestinationLevel() { return destinationLevel; }
    public int getDestinationX() { return destinationX; }
    public int getDestinationY() { return destinationY; }
    public String getDestinationPortalId() { return destinationPortalId; }
    public boolean requiresKey() { return requiresKey; }
    public String getRequiredKeyId() { return requiredKeyId; }
    public boolean requiresQuest() { return requiresQuest; }
    public String getRequiredQuestId() { return requiredQuestId; }
    public boolean isAutoTeleport() { return autoTeleport; }
    public int getCooldownTime() { return cooldownTime; }
    public int getCurrentCooldown() { return currentCooldown; }
    public boolean isOnCooldown() { return currentCooldown > 0; }
    public boolean isOneTimeUse() { return oneTimeUse; }
    
    /**
     * ABSTRACT METHODS: The game class MUST implement these methods
     */
    
    /**
     * Configures the specific animations for the portal.
     * 
     * @param animator The Animator component to be configured.
     */
    protected abstract void setupAnimations(Animator animator);
    
    /**
     * Checks if the player possesses the required key to use this portal.
     * 
     * @return true if the key is held or if no key is needed.
     */
    protected abstract boolean hasRequiredKey();
    
    /**
     * Checks if the player has completed the required quest to use this portal.
     * 
     * @return true if the quest is completed or if no quest is needed.
     */
    protected abstract boolean hasRequiredQuest();
    
    /**
     * Checks if the given GameObject is the player.
     * 
     * @param gameObject The GameObject to check.
     * @return true if it is the player, false otherwise.
     */
    protected abstract boolean isPlayer(GameObject gameObject);
    
    /**
     * Executes the game-specific teleportation logic.
     * 
     * @param level The destination level name.
     * @param x The destination X coordinate.
     * @param y The destination Y coordinate.
     * @param portalId The ID of the destination portal (optional, for linking).
     */
    protected abstract void performTeleport(String level, int x, int y, String portalId);
    
    /**
     * OPTIONAL METHODS: The game class can override these for custom behavior
     */
    
    /**
     * Called when the portal is used.
     */
    protected void onPortalUsed() {
        // Default empty implementation - can be overridden
    }
    
    /**
     * Called when the portal is activated.
     */
    protected void onPortalActivated() {
        // Default empty implementation - can be overridden
    }
    
    /**
     * Called when the portal is deactivated.
     */
    protected void onPortalDeactivated() {
        // Default empty implementation - can be overridden
    }
    
    /**
     * Called when the player attempts to use a portal that requires a key without having it.
     */
    protected void onKeyRequired() {
        // Default empty implementation - can be overridden
    }
    
    /**
     * Called when the player attempts to use a portal that requires a quest without having completed it.
     */
    protected void onQuestRequired() {
        // Default empty implementation - can be overridden
    }
}