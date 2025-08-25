package com.jdstudio.engine.Object;

import com.jdstudio.engine.Graphics.Sprite.Sprite;

/**
 * Represents a short-range melee attack, such as a sword swing.
 * <p>
 * This attack creates a temporary damage area (hitbox) that lasts for a very
 * short duration. Any collidable object within this area during its active
 * frames will be affected.
 * 
 * @author JDStudio
 */
public class MeleeAttack extends GameObject {

    protected double damage;
    protected GameObject owner;
    private int lifeTime = 10; // Lives for 10 frames by default

    /**
     * Initializes the melee attack.
     *
     * @param owner    The GameObject that initiated this attack.
     * @param x        The x-coordinate of the attack area.
     * @param y        The y-coordinate of the attack area.
     * @param width    The width of the attack's hitbox.
     * @param height   The height of the attack's hitbox.
     * @param damage   The damage the attack inflicts.
     * @param lifeTime The lifetime of the hitbox in frames.
     * @param sprite   An optional sprite for debugging or visual effect.
     */
    public MeleeAttack(GameObject owner, double x, double y, int width, int height, double damage, int lifeTime, Sprite sprite) {
        super(new org.json.JSONObject());
        this.owner = owner;
        this.x = x;
        this.y = y;
        this.damage = damage;
        this.lifeTime = lifeTime;
        this.sprite = sprite;

        setCollisionType(CollisionType.DAMAGE_SOURCE);
        setCollisionMask(0, 0, width, height);
    }

    @Override
    public void tick() {
        super.tick();
        lifeTime--;
        if (lifeTime <= 0) {
            this.isDestroyed = true;
        }
    }

    /**
     * Gets the GameObject that owns (initiated) this attack.
     * @return The owner GameObject.
     */
    public GameObject getOwner() {
        return this.owner;
    }

    /**
     * Gets the damage value of this attack.
     * @return The damage amount.
     */
    public double getDamage() {
        return this.damage;
    }
}
