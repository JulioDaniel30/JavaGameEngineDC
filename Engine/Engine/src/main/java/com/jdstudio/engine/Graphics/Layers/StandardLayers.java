package com.jdstudio.engine.Graphics.Layers;

/**
 * Defines the instances of the standard rendering layers provided by the engine.
 * These layers are ordered by their depth, determining the rendering order.
 */
public final class StandardLayers {
    private StandardLayers() {} // Ensures the class cannot be instantiated
    
    /** The farthest background layer (depth 0). Used for parallax backgrounds. */
    public static final RenderLayer PARALLAX_BACKGROUND = new RenderLayer("PARALLAX_BACKGROUND", 0);
    
    /** Background tiles and static world elements (depth 10). */
    public static final RenderLayer WORLD_BACKGROUND = new RenderLayer("WORLD_BACKGROUND", 10);
    
    /** Game objects that appear behind the player (depth 20). */
    public static final RenderLayer GAMEPLAY_BELOW = new RenderLayer("GAMEPLAY_BELOW", 20);
    
    /** The player and enemies (depth 30). */
    public static final RenderLayer CHARACTERS = new RenderLayer("CHARACTERS", 30);
    
    /** Game objects that appear in front of the player (depth 40). */
    public static final RenderLayer GAMEPLAY_ABOVE = new RenderLayer("GAMEPLAY_ABOVE", 40);
    
    /** Projectiles, ensuring they appear above characters (depth 50). */
    public static final RenderLayer PROJECTILES = new RenderLayer("PROJECTILES", 50);
    
    /** Particles, such as explosions, that should appear over everything else (depth 60). */
    public static final RenderLayer PARTICLES = new RenderLayer("PARTICLES", 60);
    
    /** Foreground tiles (e.g., tree canopies) (depth 70). */
    public static final RenderLayer WORLD_FOREGROUND = new RenderLayer("WORLD_FOREGROUND", 70);
    
    /** The lighting mask (depth 80). */
    public static final RenderLayer LIGHTING = new RenderLayer("LIGHTING", 80);
    
    /** Popups and temporary UI elements (depth 90). */
    public static final RenderLayer POPUPS = new RenderLayer("POPUPS", 90);
    
    /** The main User Interface (depth 100). */
    public static final RenderLayer UI = new RenderLayer("UI", 100);
}
