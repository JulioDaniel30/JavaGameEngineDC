package com.jdstudio.engine.Object;

import java.awt.Color;
import java.awt.Graphics;

import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Graphics.Sprite.Sprite;

/**
 * Represents an instantaneous ray-like attack, such as a laser beam.
 * <p>
 * This attack is typically represented by a shape (like a thin rectangle)
 * that exists for a very short duration, dealing damage to any object
 * it collides with upon creation. It can be rendered using a sprite or
 * by drawing a colored rectangle directly.
 * 
 * @author JDStudio
 */
public class RayAttack extends GameObject {

    protected double damage;
    protected GameObject owner;
    private int lifeTime = 2; // Exists for a very short time (e.g., 2 frames)
    private Color drawColor;  // Color to use for drawing if no sprite is provided

    /**
     * Initializes the ray attack using a sprite for rendering.
     *
     * @param owner  The GameObject that fired this attack.
     * @param startX The initial x-coordinate.
     * @param startY The initial y-coordinate.
     * @param width  The width of the ray's collision box.
     * @param height The height of the ray's collision box.
     * @param damage The damage the attack inflicts.
     * @param sprite The sprite to render for the ray.
     */
    public RayAttack(GameObject owner, double startX, double startY, double width, double height, double damage, Sprite sprite) {
        super(new org.json.JSONObject());
        this.owner = owner;
        this.x = startX;
        this.y = startY;
        this.width = (int) width;
        this.height = (int) height;
        this.damage = damage;
        this.sprite = sprite;
        this.drawColor = null; // Using sprite, so no color needed
        
        setCollisionType(CollisionType.DAMAGE_SOURCE);
        setCollisionMask(0, 0, (int)width, (int)height);
    }

    /**
     * Initializes the ray attack by drawing a colored rectangle.
     *
     * @param owner  The GameObject that fired this attack.
     * @param startX The initial x-coordinate.
     * @param startY The initial y-coordinate.
     * @param width  The width of the ray's collision box.
     * @param height The height of the ray's collision box.
     * @param damage The damage the attack inflicts.
     * @param color  The color to use for drawing the ray.
     */
    public RayAttack(GameObject owner, double startX, double startY, double width, double height, double damage, Color color) {
        super(new org.json.JSONObject());
        this.owner = owner;
        this.x = startX;
        this.y = startY;
        this.width = (int) width;
        this.height = (int) height;
        this.damage = damage;
        this.sprite = null; // Using color, so no sprite needed
        this.drawColor = color;
        
        setCollisionType(CollisionType.DAMAGE_SOURCE);
        setCollisionMask(0, 0, (int)width, (int)height);
    }

    @Override
    public void tick() {
        super.tick();
        lifeTime--;
        if (lifeTime <= 0) {
            this.isDestroyed = true;
        }
    }

    @Override
    public void render(Graphics g) {
        if (isDestroyed) return;

        // If a color is defined, draw a rectangle
        if (this.drawColor != null) {
            g.setColor(this.drawColor);
            g.fillRect(getX() - Engine.camera.getX(), getY() - Engine.camera.getY(), getWidth(), getHeight());
        } 
        // Otherwise, fall back to the default sprite rendering from the parent GameObject
        else {
            super.render(g);
        }
        
        // We still call renderDebug from the parent if debug mode is on
        // Note: super.render() also calls renderDebug, so we only call it here in the color-drawing case.
        if (this.drawColor != null && Engine.isDebug) {
            renderDebug(g);
        }
    }

    /**
     * Gets the GameObject that owns (fired) this attack.
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