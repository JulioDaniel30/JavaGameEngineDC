package com.jdstudio.engine.Object;

import com.jdstudio.engine.Graphics.Layers.RenderManager;
import com.jdstudio.engine.Graphics.Sprite.Sprite;

/**
 * An abstract base class for projectiles in the game.
 * It handles their movement, lifetime, and basic collision properties.
 * Projectiles are typically managed by a {@link ProjectileManager}.
 * 
 * @author JDStudio
 */
public abstract class BaseProjectile extends GameObject {

    /** Flag indicating if the projectile is currently active. */
    public boolean isActive = false;
    /** The x-component of the projectile's movement direction. */
    protected double directionX;
    /** The y-component of the projectile's movement direction. */
    protected double directionY;
    /** The speed of the projectile. */
    protected double speed;
    /** The amount of damage the projectile inflicts. */
    protected double damage;
    /** The remaining lifetime of the projectile in frames. */
    protected int lifeTime = 60;
    /** The GameObject that fired this projectile. */
    protected GameObject owner;
    /** Flag indicating if the projectile has just been spawned in the current frame. */
    private boolean justSpawned = false;

    /**
     * Protected constructor, as this class should not be instantiated directly.
     * Initializes the projectile with a TRIGGER collision type.
     */
    protected BaseProjectile() {
        super(new org.json.JSONObject()); // Pass an empty JSON to the parent
        setCollisionType(CollisionType.TRIGGER);
    }

    /**
     * Initializes the projectile with its properties.
     * This method is typically called when a projectile is spawned from a pool.
     *
     * @param owner    The GameObject that fired this projectile.
     * @param startX   The initial x-coordinate of the projectile.
     * @param startY   The initial y-coordinate of the projectile.
     * @param dirX     The x-component of the direction vector.
     * @param dirY     The y-component of the direction vector.
     * @param speed    The speed of the projectile.
     * @param damage   The damage the projectile inflicts.
     * @param lifeTime The lifetime of the projectile in frames.
     * @param sprite   The sprite to render for the projectile.
     */
    public void init(GameObject owner, double startX, double startY, double dirX, double dirY,
                     double speed, double damage, int lifeTime, Sprite sprite) {
        this.owner = owner;
        this.x = startX;
        this.y = startY;
        this.directionX = dirX;
        this.directionY = dirY;
        this.speed = speed;
        this.damage = damage;
        this.lifeTime = lifeTime;
        this.sprite = sprite;
        this.isActive = true;
        this.justSpawned = true; 
        this.isDestroyed = false;

        if (sprite != null) {
            setCollisionMask(0, 0, sprite.getWidth(), sprite.getHeight());
        }
    }

    /**
     * Updates the projectile's position and lifetime.
     * If its lifetime runs out, it deactivates itself.
     */
    @Override
    public void tick() {
        if (!isActive) return;

        x += directionX * speed;
        y += directionY * speed;

        lifeTime--;
        if (lifeTime <= 0) {
            deactivate();
        }
    }
    
    /**
     * Called by the ProjectileManager to signal that the projectile's first frame has passed.
     * This is used to prevent immediate self-collision upon spawning.
     */
    public void onFramePassed() {
        this.justSpawned = false;
    }

    /**
     * Checks if the projectile has just been spawned in the current frame.
     * @return true if it just spawned, false otherwise.
     */
    public boolean hasJustSpawned() {
        return this.justSpawned;
    }

    /**
     * Deactivates the projectile, making it inactive, unregistering it from the RenderManager,
     * and marking it for destruction.
     */
    public void deactivate() {
        this.isActive = false;
        RenderManager.getInstance().unregister(this);
        this.isDestroyed = true;
    }
    
    /**
     * Gets the GameObject that owns (fired) this projectile.
     * @return The owner GameObject.
     */
    public GameObject getOwner() { 
        return this.owner; 
    }

    /**
     * Gets the damage value of this projectile.
     * @return The damage amount.
     */
    public double getDamage() { 
        return this.damage; 
    }
}
