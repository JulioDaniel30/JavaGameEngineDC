package com.jdstudio.engine.Object.PreBuildObjcts;

import org.json.JSONObject;

import com.jdstudio.engine.Components.InteractionComponent;
import com.jdstudio.engine.Components.InteractionZone;
import com.jdstudio.engine.Core.ISavable;
import com.jdstudio.engine.Graphics.Sprite.Animations.Animator;
import com.jdstudio.engine.Object.GameObject;
import com.jdstudio.engine.Utils.PropertiesReader;

/**
 * An abstract base class for any type of destructible barrier in the game.
 * It contains all the logic for health, damage, destruction, regeneration, and animation.
 * Game-specific subclasses are responsible for providing the specific animations and defining behavior.
 * This class also implements {@link ISavable} to allow its state to be persisted.
 * 
 * @author JDStudio
 */
public abstract class EngineBarrier extends GameObject implements ISavable {

    protected double health;
    protected double maxHealth;
    protected boolean isDestroyed = false;
    protected Animator animator;
    protected String barrierId;
    protected boolean requiresSpecialWeapon = false;
    protected String requiredWeaponType;
    protected boolean canRegenerate = false;
    protected double regenerationRate = 0.0; // Health per tick
    protected int regenerationDelay = 0; // Ticks without damage before regenerating
    protected int timeSinceLastDamage = 0;
    protected boolean dropsLoot = false;
    protected String lootTable;

    /**
     * Constructs a new EngineBarrier with the given properties.
     * 
     * @param properties A JSONObject containing the initial properties of the barrier.
     */
    public EngineBarrier(JSONObject properties) {
        super(properties);
    }

    /**
     * Initializes the EngineBarrier's properties from a JSONObject.
     * It sets up health, regeneration, loot, and interaction components.
     *
     * @param properties A JSONObject containing the properties to initialize.
     */
    @Override
    public void initialize(JSONObject properties) {
        super.initialize(properties);
        PropertiesReader reader = new PropertiesReader(properties);
        
        this.maxHealth = reader.getDouble("maxHealth", 100.0);
        this.health = reader.getDouble("health", maxHealth);
        this.barrierId = reader.getString("barrierId", "");
        this.requiresSpecialWeapon = reader.getBoolean("requiresSpecialWeapon", false);
        this.requiredWeaponType = reader.getString("requiredWeaponType", "");
        this.canRegenerate = reader.getBoolean("canRegenerate", false);
        this.regenerationRate = reader.getDouble("regenerationRate", 0.1);
        this.regenerationDelay = reader.getInt("regenerationDelay", 300); // 5 seconds at 60 FPS
        this.dropsLoot = reader.getBoolean("dropsLoot", false);
        this.lootTable = reader.getString("lootTable", "");
        this.isDestroyed = reader.getBoolean("isDestroyed", false);

        this.animator = new Animator();
        this.addComponent(animator);
        
        // Call the abstract method that the GAME class will implement
        setupAnimations(this.animator);

        // Add interaction zone for manual attacks (optional)
        InteractionComponent interaction = new InteractionComponent();
        interaction.addZone(new InteractionZone(this, InteractionZone.TYPE_DIALOGUE, 24.0));
        this.addComponent(interaction);

        updateStateVisuals();
        setCollisionType(isDestroyed ? CollisionType.NO_COLLISION : CollisionType.SOLID);
    }
    
    /**
     * Main interaction method with the barrier (manual attack).
     * It checks for special weapon requirements and applies damage.
     */
    public void interact() {
        if (isDestroyed) return;
        if (!animator.getCurrentAnimationKey().startsWith("idle")) return;

        // Check if special weapon is required
        if (requiresSpecialWeapon && !hasRequiredWeapon()) {
            onSpecialWeaponRequired();
            return;
        }

        // Apply damage based on player's weapon
        double damage = getPlayerWeaponDamage();
        takeDamage(damage);
    }

    /**
     * Applies damage to the barrier.
     * It checks for valid damage types and triggers destruction if health drops to zero.
     *
     * @param damage The amount of damage to apply.
     */
    public void takeDamage(double damage) {
        if (isDestroyed || damage <= 0) return;

        // Check if damage type is valid
        if (requiresSpecialWeapon && !isValidDamageType()) {
            onInvalidDamageType();
            return;
        }

        health -= damage;
        timeSinceLastDamage = 0;
        
        // Notify that the barrier took damage
        onBarrierDamaged(damage);

        if (health <= 0) {
            destroyBarrier();
        } else {
            // Play damage animation if not destroyed
            animator.play("damaged");
        }
    }

    /**
     * Destroys the barrier.
     * It sets the destroyed flag, changes collision type, plays destruction animation, and drops loot.
     */
    protected void destroyBarrier() {
        if (isDestroyed) return;
        
        isDestroyed = true;
        health = 0;
        animator.play("destroying");
        setCollisionType(CollisionType.NO_COLLISION);
        
        // Drop loot if configured
        if (dropsLoot && !lootTable.isEmpty()) {
            dropLoot(lootTable);
        }
        
        // Notify that the barrier was destroyed
        onBarrierDestroyed();
    }

    /**
     * Regenerates the barrier's health if regeneration is enabled and enough time has passed since last damage.
     */
    protected void regenerateHealth() {
        if (!canRegenerate || isDestroyed) return;
        if (timeSinceLastDamage < regenerationDelay) return;
        
        if (health < maxHealth) {
            health += regenerationRate;
            if (health > maxHealth) {
                health = maxHealth;
            }
            onBarrierRegenerated();
        }
    }

    /**
     * Updates the barrier's logic, including regeneration and animation state transitions.
     */
    @Override
    public void tick() {
        super.tick();
        
        if (!isDestroyed) {
            timeSinceLastDamage++;
            regenerateHealth();
        }
        
        // Manage animations
        if (animator.getCurrentAnimation() != null && animator.getCurrentAnimation().hasFinished()) {
            String currentKey = animator.getCurrentAnimationKey();
            
            if ("damaged".equals(currentKey)) {
                updateStateVisuals();
            } else if ("destroying".equals(currentKey)) {
                animator.play("destroyed");
                // Can mark for complete removal from the game if necessary
                // this.isDestroyed = true;
            }
        }
    }
    
    /**
     * Updates the visual state of the barrier based on its current health percentage.
     * It plays different idle animations (e.g., intact, damaged, heavily damaged, critical).
     */
    private void updateStateVisuals() {
        if (isDestroyed) {
            animator.play("destroyed");
            return;
        }
        
        // Different visual states based on health
        double healthPercentage = health / maxHealth;
        
        if (healthPercentage > 0.75) {
            animator.play("idleIntact");
        } else if (healthPercentage > 0.5) {
            animator.play("idleDamaged");
        } else if (healthPercentage > 0.25) {
            animator.play("idleHeavilyDamaged");
        } else {
            animator.play("idleCritical");
        }
    }

    /**
     * Saves the current state of the barrier to a JSONObject.
     * 
     * @return A JSONObject containing the barrier's savable state.
     */
    @Override
    public JSONObject saveState() {
        JSONObject state = new JSONObject();
        state.put("name", this.name);
        state.put("health", this.health);
        state.put("isDestroyed", this.isDestroyed);
        state.put("timeSinceLastDamage", this.timeSinceLastDamage);
        return state;
    }

    /**
     * Loads the state of the barrier from a JSONObject.
     * 
     * @param state The JSONObject containing the saved data.
     */
    @Override
    public void loadState(JSONObject state) {
        this.health = state.getDouble("health");
        this.isDestroyed = state.getBoolean("isDestroyed");
        this.timeSinceLastDamage = state.getInt("timeSinceLastDamage");
        updateStateVisuals();
        setCollisionType(isDestroyed ? CollisionType.NO_COLLISION : CollisionType.SOLID);
    }
    
    // Getters
    public double getHealth() { return health; }
    public double getMaxHealth() { return maxHealth; }
    public boolean isDestroyed() { return isDestroyed; }
    public String getBarrierId() { return barrierId; }
    public boolean requiresSpecialWeapon() { return requiresSpecialWeapon; }
    public String getRequiredWeaponType() { return requiredWeaponType; }
    public boolean canRegenerate() { return canRegenerate; }
    public double getRegenerationRate() { return regenerationRate; }
    public boolean dropsLoot() { return dropsLoot; }
    public String getLootTable() { return lootTable; }
    public double getHealthPercentage() { return health / maxHealth; }
    
    /**
     * ABSTRACT METHODS: The game class MUST implement these methods
     */
    
    /**
     * Configures the specific animations for the barrier.
     * 
     * @param animator The Animator component to be configured.
     */
    protected abstract void setupAnimations(Animator animator);
    
    /**
     * Checks if the player possesses the required weapon to damage this barrier.
     * 
     * @return true if the required weapon is held or if no special weapon is needed.
     */
    protected abstract boolean hasRequiredWeapon();
    
    /**
     * Checks if the current damage type is valid for this barrier.
     * 
     * @return true if the damage is valid.
     */
    protected abstract boolean isValidDamageType();
    
    /**
     * Gets the damage value of the player's current weapon.
     * 
     * @return The damage value to be applied.
     */
    protected abstract double getPlayerWeaponDamage();
    
    /**
     * Drops loot from the destroyed barrier.
     * 
     * @param lootTable The loot table to be used.
     */
    protected abstract void dropLoot(String lootTable);
    
    /**
     * OPTIONAL METHODS: The game class can override these for custom behavior
     */
    
    /**
     * Called when the barrier takes damage.
     * 
     * @param damage The amount of damage taken.
     */
    protected void onBarrierDamaged(double damage) {
        // Default empty implementation - can be overridden
    }
    
    /**
     * Called when the barrier is destroyed.
     */
    protected void onBarrierDestroyed() {
        // Default empty implementation - can be overridden
    }
    
    /**
     * Called when the barrier regenerates health.
     */
    protected void onBarrierRegenerated() {
        // Default empty implementation - can be overridden
    }
    
    /**
     * Called when the player attempts to attack without the required special weapon.
     */
    protected void onSpecialWeaponRequired() {
        // Default empty implementation - can be overridden
    }
    
    /**
     * Called when the damage type is not valid for this barrier.
     */
    protected void onInvalidDamageType() {
        // Default empty implementation - can be overridden
    }
}