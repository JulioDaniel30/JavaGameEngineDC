package com.jdstudio.engine.Object;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.jdstudio.engine.Graphics.Sprite.Sprite;
import com.jdstudio.engine.Object.GameObject.CollisionType;
import com.jdstudio.engine.World.World;

/**
 * A singleton class responsible for managing projectiles in the game.
 * It handles spawning, updating, and collision detection for projectiles,
 * utilizing a projectile pool for efficient object reuse.
 * 
 * @author JDStudio
 */
public class ProjectileManager {

    private static final ProjectileManager instance = new ProjectileManager();
    
    /** The pool of reusable BaseProjectile objects. */
    private final List<BaseProjectile> projectilePool = new ArrayList<>();
    
    /** A factory function to create new projectile instances when the pool is exhausted. */
    private Supplier<BaseProjectile> projectileFactory;
    
    /** Reference to the game world for collision checks. */
    private World world;
    
    /** Reference to the main list of all GameObjects in the game, used for collision detection. */
    private List<GameObject> allGameObjects;

    private ProjectileManager() {}

    /**
     * Gets the single instance of the ProjectileManager.
     * @return The singleton instance.
     */
    public static ProjectileManager getInstance() {
        return instance;
    }

    /**
     * Initializes the ProjectileManager with necessary game world references.
     * This must be called before spawning any projectiles.
     *
     * @param factory      A Supplier that provides new instances of BaseProjectile.
     * @param world        The game world instance.
     * @param gameObjects  The main list of all GameObjects in the game.
     */
    public void init(Supplier<BaseProjectile> factory, World world, List<GameObject> gameObjects) {
        this.projectileFactory = factory;
        this.world = world;
        this.allGameObjects = gameObjects;
    }

    /**
     * Spawns a new projectile into the game world.
     * It reuses an inactive projectile from the pool if available, otherwise creates a new one.
     * The projectile is added to the main game object list for collision processing.
     *
     * @param owner    The GameObject that fired this projectile.
     * @param startX   The initial x-coordinate.
     * @param startY   The initial y-coordinate.
     * @param dirX     The x-component of the direction vector.
     * @param dirY     The y-component of the direction vector.
     * @param speed    The speed of the projectile.
     * @param damage   The damage the projectile inflicts.
     * @param lifeTime The lifetime of the projectile in frames.
     * @param sprite   The sprite to render for the projectile.
     */
    public void spawnProjectile(GameObject owner, double startX, double startY, double dirX, double dirY,
                                double speed, int damage, int lifeTime, Sprite sprite) {
        if (projectileFactory == null) {
            System.err.println("ERROR: ProjectileManager has not been initialized!");
            return;
        }

        BaseProjectile projectileToSpawn = null;
        for (BaseProjectile p : projectilePool) {
            if (!p.isActive) {
                projectileToSpawn = p;
                break;
            }
        }
        
        if (projectileToSpawn == null) {
            projectileToSpawn = projectileFactory.get();
            projectilePool.add(projectileToSpawn);
        }
        
        projectileToSpawn.init(owner, startX, startY, dirX, dirY, speed, damage, lifeTime, sprite);
        
        // Add the projectile to the main game object list,
        // ensuring it is seen by the collision system immediately.
        if (!allGameObjects.contains(projectileToSpawn)) {
            allGameObjects.add(projectileToSpawn);
        }
    }

    /**
     * Updates all active projectiles, handling their movement and collision detection.
     * Projectiles collide with world tiles and other GameObjects (Characters, Solids).
     */
    public void update() {
        if (world == null || allGameObjects == null) return;
        
        // Use an iterator to safely remove items during the loop
        for (BaseProjectile p : projectilePool) {
            if (p.isActive) {
                p.tick();

                // Check collision with world tiles
                if (!world.isFree(p.getX(), p.getY(), p.getMaskX(), p.getMaskY(), p.getMaskWidth(), p.getMaskHeight())) {
                    p.deactivate();
                }

                // Check collision with other GameObjects
                for (GameObject other : allGameObjects) {
                    // A projectile should not collide with itself or its owner, or destroyed objects
                    if (other == p || other == p.getOwner() || other.isDestroyed) continue;
                    
                    // Check for collision with solid objects or characters
                    if ((other.getCollisionType() == CollisionType.SOLID
                    		|| other.getCollisionType() == CollisionType.CHARACTER_TRIGGER
                    		|| other.getCollisionType() == CollisionType.CHARACTER_SOLID) && GameObject.isColliding(p, other)) {
                        
                        // If the other object is a Character, apply damage
                        if (other instanceof Character) {
                            ((Character) other).takeDamage(p.getDamage());
                        }
                        p.deactivate(); // Deactivate projectile on hit
                        break; 
                    }
                }
            }
        }
    }

    /**
     * Resets the ProjectileManager, deactivating all projectiles and clearing the pool.
     * Also removes them from the main game object list.
     */
    public void reset() {
        // When resetting, also remove all projectiles from the game's main list
        for(BaseProjectile p : projectilePool) {
            allGameObjects.remove(p);
        }
        projectilePool.clear();
    }
}
