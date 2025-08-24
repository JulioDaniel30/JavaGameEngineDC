package com.jdstudio.engine.Graphics.Effects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;

/**
 * Represents a single particle in a particle system.
 * Particles have properties such as position, velocity, color, size, and a limited lifetime.
 * They are typically managed by a ParticleManager and reused from a pool.
 * 
 * @author JDStudio
 */
public class Particle {

    /** Flag indicating if the particle is currently active and should be updated/rendered. */
    public boolean isActive = false;

    private Point2D.Double position;
    private Point2D.Double velocity;
    
    private Color startColor, endColor, currentColor;
    private float startSize, endSize, currentSize;
    
    private int life, maxLife;
    
    /**
     * Constructs a new Particle.
     * Initializes position and velocity to default values.
     */
    public Particle() {
        this.position = new Point2D.Double();
        this.velocity = new Point2D.Double();
    }

    /**
     * Initializes or "wakes up" a particle from the pool with new properties.
     * This method is used to reset a particle's state for reuse.
     *
     * @param x          The initial x-coordinate of the particle.
     * @param y          The initial y-coordinate of the particle.
     * @param velX       The initial x-velocity of the particle.
     * @param velY       The initial y-velocity of the particle.
     * @param startSize  The initial size of the particle.
     * @param endSize    The final size of the particle (at the end of its life).
     * @param startColor The initial color of the particle.
     * @param endColor   The final color of the particle (at the end of its life).
     * @param life       The maximum lifetime of the particle in ticks.
     */
    public void init(double x, double y, double velX, double velY, float startSize, float endSize,
                     Color startColor, Color endColor, int life) {
        this.position.setLocation(x, y);
        this.velocity.setLocation(velX, velY);
        this.startSize = startSize;
        this.endSize = endSize;
        this.currentSize = startSize;
        this.startColor = startColor;
        this.endColor = endColor;
        this.currentColor = startColor;
        this.maxLife = life;
        this.life = life;
        this.isActive = true;
    }

    /**
     * Updates the particle's logic each frame.
     * It decrements the lifetime, updates position based on velocity,
     * and interpolates color and size based on its remaining life.
     */
    public void update() {
        if (!isActive) return;

        life--;
        if (life <= 0) {
            isActive = false;
            return;
        }

        // Update position
        position.x += velocity.x;
        position.y += velocity.y;
        
        // Linear interpolation for size and color
        float lifeRatio = (float) life / (float) maxLife; // 1.0 at start, 0.0 at end

        // Size
        currentSize = endSize + (startSize - endSize) * lifeRatio;
        
        // Color (interpolate each channel: R, G, B, A)
        int r = (int) (endColor.getRed() + (startColor.getRed() - endColor.getRed()) * lifeRatio);
        int g = (int) (endColor.getGreen() + (startColor.getGreen() - endColor.getGreen()) * lifeRatio);
        int b = (int) (endColor.getBlue() + (startColor.getBlue() - endColor.getBlue()) * lifeRatio);
        int a = (int) (endColor.getAlpha() + (startColor.getAlpha() - endColor.getAlpha()) * lifeRatio);
        
        currentColor = new Color(r, g, b, a);
    }

    /**
     * Renders the particle on the screen.
     * 
     * @param g       The Graphics context to draw on.
     * @param cameraX The x-coordinate of the camera's top-left corner.
     * @param cameraY The y-coordinate of the camera's top-left corner.
     */
    public void render(Graphics g, int cameraX, int cameraY) {
        if (!isActive || currentSize <= 0) return;
        
        g.setColor(currentColor);
        
        // Calculate rendering position relative to camera
        int drawX = (int) (position.x - (currentSize / 2)) - cameraX;
        int drawY = (int) (position.y - (currentSize / 2)) - cameraY;
        
        g.fillOval(drawX, drawY, (int)currentSize, (int)currentSize);
    }
}
