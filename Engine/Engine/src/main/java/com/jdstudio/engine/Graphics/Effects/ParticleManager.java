package com.jdstudio.engine.Graphics.Effects;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.jdstudio.engine.Engine;

/**
 * A singleton class that manages a pool of particles for various visual effects.
 * It provides methods to update and render all active particles, and to emit
 * different types of particle effects (e.g., explosions).
 * 
 * @author JDStudio
 */
public class ParticleManager {

    private static final ParticleManager instance = new ParticleManager();
    
    /** The pool of particles, reused to avoid constant object creation. */
    private final List<Particle> particlePool = new ArrayList<>();
    
    /** Random number generator for particle properties. */
    private final Random random = new Random();

    private ParticleManager() {}

    /**
     * Gets the single instance of the ParticleManager.
     * @return The singleton instance.
     */
    public static ParticleManager getInstance() {
        return instance;
    }

    /**
     * Retrieves an inactive particle from the pool for reuse.
     * If no inactive particles are available, a new one is created and added to the pool.
     * @return An inactive Particle object ready for initialization.
     */
    private Particle getInactiveParticle() {
        for (Particle p : particlePool) {
            if (!p.isActive) {
                return p;
            }
        }
        // If no inactive particles, create a new one
        Particle newParticle = new Particle();
        particlePool.add(newParticle);
        return newParticle;
    }

    /**
     * Updates all active particles in the pool.
     * This method should be called once per game frame.
     */
    public void update() {
        for (Particle p : particlePool) {
            if (p.isActive) {
                p.update();
            }
        }
    }
    
    /**
     * Renders all active particles.
     * This method should be called once per game frame after all other game objects are rendered.
     * 
     * @param g The Graphics context to draw on.
     */
    public void render(Graphics g) {
        int camX = Engine.camera.getX();
        int camY = Engine.camera.getY();
        for (Particle p : particlePool) {
            // Render only if active
            p.render(g, camX, camY);
        }
    }

    // --- EMISSION METHODS (EXAMPLES) ---

    /**
     * Creates an explosion effect by emitting multiple particles.
     *
     * @param x          Initial X position of the explosion.
     * @param y          Initial Y position of the explosion.
     * @param count      Number of particles to emit.
     * @param startColor Initial color of the particles (e.g., Color.YELLOW).
     * @param endColor   Final color of the particles (e.g., new Color(100, 0, 0, 0) - dark transparent red).
     * @param minLife    Minimum lifetime of particles in frames.
     * @param maxLife    Maximum lifetime of particles in frames.
     * @param minSpeed   Minimum initial speed of particles.
     * @param maxSpeed   Maximum initial speed of particles.
     * @param startSize  Initial size of particles.
     * @param endSize    Final size of particles.
     */
    public void createExplosion(double x, double y, int count, Color startColor, Color endColor,
                                int minLife, int maxLife, double minSpeed, double maxSpeed,
                                float startSize, float endSize) {
        for (int i = 0; i < count; i++) {
            Particle p = getInactiveParticle();
            
            double angle = random.nextDouble() * 2 * Math.PI;
            double speed = minSpeed + random.nextDouble() * (maxSpeed - minSpeed);
            
            double velX = Math.cos(angle) * speed;
            double velY = Math.sin(angle) * speed;
            
            int life = minLife + random.nextInt(maxLife - minLife + 1);
            
            p.init(x, y, velX, velY, startSize, endSize, startColor, endColor, life);
        }
    }
}
