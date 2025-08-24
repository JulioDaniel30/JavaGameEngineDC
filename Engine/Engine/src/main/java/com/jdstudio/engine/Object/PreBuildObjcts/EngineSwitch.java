package com.jdstudio.engine.Object.PreBuildObjcts;

import org.json.JSONObject;

import com.jdstudio.engine.Components.InteractionComponent;
import com.jdstudio.engine.Components.InteractionZone;
import com.jdstudio.engine.Core.ISavable;
import com.jdstudio.engine.Graphics.Sprite.Animations.Animator;
import com.jdstudio.engine.Object.GameObject;
import com.jdstudio.engine.Utils.PropertiesReader;

/**
 * An abstract base class for any type of switch or lever in the game.
 * It contains all the logic for state (on/off), animation, and event system.
 * Game-specific subclasses are responsible for providing the specific animations and defining the actions.
 * This class also implements {@link ISavable} to allow its state to be persisted.
 * 
 * @author JDStudio
 */
public abstract class EngineSwitch extends GameObject implements ISavable {

    protected boolean isOn = false;
    protected Animator animator;
    protected String switchId;
    protected boolean isToggleable = true;
    protected boolean requiresKey = false;
    protected String requiredKeyId;
    protected int cooldownTime = 0; // In ticks
    protected int currentCooldown = 0;

    /**
     * Constructs a new EngineSwitch with the given properties.
     * 
     * @param properties A JSONObject containing the initial properties of the switch.
     */
    public EngineSwitch(JSONObject properties) {
        super(properties);
    }

    /**
     * Initializes the EngineSwitch's properties from a JSONObject.
     * It sets up the initial state, ID, toggleability, key requirements, and cooldown.
     *
     * @param properties A JSONObject containing the properties to initialize.
     */
    @Override
    public void initialize(JSONObject properties) {
        super.initialize(properties);
        PropertiesReader reader = new PropertiesReader(properties);
        
        this.isOn = reader.getBoolean("startsOn", false);
        this.switchId = reader.getString("switchId", "");
        this.isToggleable = reader.getBoolean("isToggleable", true);
        this.requiresKey = reader.getBoolean("requiresKey", false);
        this.requiredKeyId = reader.getString("requiredKeyId", "");
        this.cooldownTime = reader.getInt("cooldownTime", 0);

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
     * Main interaction method with the switch.
     * It checks for cooldown and key requirements, then toggles the switch state or activates it.
     */
    public void interact() {
        if (!animator.getCurrentAnimationKey().startsWith("idle")) return;
        if (currentCooldown > 0) return;

        // Check if key is required
        if (requiresKey && !hasRequiredKey()) {
            onKeyRequired();
            return;
        }

        // If toggleable, switch the state
        if (isToggleable) {
            if (isOn) {
                turnOff();
            } else {
                turnOn();
            }
        } else {
            // If not toggleable, just activate temporarily
            if (!isOn) {
                turnOn();
            }
        }
    }

    /**
     * Turns the switch to the "on" state.
     * It plays the turningOn animation, sets cooldown, and triggers game-specific activation actions.
     */
    public void turnOn() {
        if (isOn) return;
        
        isOn = true;
        animator.play("turningOn");
        currentCooldown = cooldownTime;
        
        // Call the abstract method to execute the game-specific action
        onSwitchActivated();
        
        // Notify that the switch was turned on
        onSwitchTurnedOn();
    }

    /**
     * Turns the switch to the "off" state.
     * It plays the turningOff animation, sets cooldown, and triggers game-specific deactivation actions.
     */
    public void turnOff() {
        if (!isOn) return;
        
        isOn = false;
        animator.play("turningOff");
        currentCooldown = cooldownTime;
        
        // Call the abstract method to execute the game-specific action
        onSwitchDeactivated();
        
        // Notify that the switch was turned off
        onSwitchTurnedOff();
    }

    /**
     * Updates the switch's logic, primarily managing cooldown timers and animation state transitions.
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
            
            if ("turningOn".equals(currentKey)) {
                animator.play("idleOn");
            } else if ("turningOff".equals(currentKey)) {
                animator.play("idleOff");
            }
        }
    }
    
    /**
     * Updates the visual state of the switch based on its current on/off status.
     * It plays different idle animations (e.g., idleOn, idleOff).
     */
    private void updateStateVisuals() {
        if (isOn) {
            animator.play("idleOn");
        } else {
            animator.play("idleOff");
        }
    }

    /**
     * Saves the current state of the switch to a JSONObject.
     * 
     * @return A JSONObject containing the switch's savable state.
     */
    @Override
    public JSONObject saveState() {
        JSONObject state = new JSONObject();
        state.put("name", this.name);
        state.put("isOn", this.isOn);
        state.put("currentCooldown", this.currentCooldown);
        return state;
    }

    /**
     * Loads the state of the switch from a JSONObject.
     * 
     * @param state The JSONObject containing the saved data.
     */
    @Override
    public void loadState(JSONObject state) {
        this.isOn = state.getBoolean("isOn");
        this.currentCooldown = state.getInt("currentCooldown");
        updateStateVisuals();
    }
    
    // Getters
    public boolean isOn() { return isOn; }
    public String getSwitchId() { return switchId; }
    public boolean isToggleable() { return isToggleable; }
    public boolean requiresKey() { return requiresKey; }
    public String getRequiredKeyId() { return requiredKeyId; }
    public int getCooldownTime() { return cooldownTime; }
    public int getCurrentCooldown() { return currentCooldown; }
    public boolean isOnCooldown() { return currentCooldown > 0; }
    
    /**
     * ABSTRACT METHODS: The game class MUST implement these methods
     */
    
    /**
     * Configures the specific animations for the switch.
     * 
     * @param animator The Animator component to be configured.
     */
    protected abstract void setupAnimations(Animator animator);
    
    /**
     * Checks if the player possesses the required key to operate this switch.
     * 
     * @return true if the key is held or if no key is needed.
     */
    protected abstract boolean hasRequiredKey();
    
    /**
     * Executed when the switch is activated (turned on).
     */
    protected abstract void onSwitchActivated();
    
    /**
     * Executed when the switch is deactivated (turned off).
     */
    protected abstract void onSwitchDeactivated();
    
    /**
     * OPTIONAL METHODS: The game class can override these for custom behavior
     */
    
    /**
     * Called when the switch is turned on.
     */
    protected void onSwitchTurnedOn() {
        // Default empty implementation - can be overridden
    }
    
    /**
     * Called when the switch is turned off.
     */
    protected void onSwitchTurnedOff() {
        // Default empty implementation - can be overridden
    }
    
    /**
     * Called when the player attempts to use a switch that requires a key without having it.
     */
    protected void onKeyRequired() {
        // Default empty implementation - can be overridden
    }
}