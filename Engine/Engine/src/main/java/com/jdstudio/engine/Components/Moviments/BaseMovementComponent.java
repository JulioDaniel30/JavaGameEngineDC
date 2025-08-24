package com.jdstudio.engine.Components.Moviments;

import java.awt.Rectangle;
import java.util.List;

import com.jdstudio.engine.Components.Component;
import com.jdstudio.engine.Events.EngineEvent;
import com.jdstudio.engine.Events.EventManager;
import com.jdstudio.engine.Events.WorldLoadedEventData;
import com.jdstudio.engine.Object.GameObject;
import com.jdstudio.engine.Object.GameObject.CollisionType;
import com.jdstudio.engine.World.World;

/**
 * Base abstract class for movement components. It provides the basic attributes
 * and methods for moving a GameObject within the game world, including speed,
 * collision detection, and world boundaries.
 * 
 * This component automatically subscribes to the WORLD_LOADED event to configure
 * itself with the world and game object references when a new map is loaded.
 * 
 * @author JDStudio
 */
public abstract class BaseMovementComponent extends Component {

    /**
     * The game world/map instance.
     */
    protected World world;
    
    /**
     * A list of all other GameObjects in the world, used for collision checks.
     */
    protected List<GameObject> allGameObjects;
    
    /**
     * The movement speed of the GameObject.
     */
    public double speed;

    /**
     * Remainder for sub-pixel movement on the X axis to ensure smooth motion.
     */
    protected double xRemainder = 0.0;
    
    /**
     * Remainder for sub-pixel movement on the Y axis to ensure smooth motion.
     */
    protected double yRemainder = 0.0;
    
    /**
     * The change in the X coordinate for the current frame.
     */
    protected double dx = 0;
    
    /**
     * The change in the Y coordinate for the current frame.
     */
    protected double dy = 0;

    /**
     * Constructs a new BaseMovementComponent.
     * 
     * @param speed The movement speed for the GameObject.
     */
    public BaseMovementComponent(double speed) {
        this.speed = speed;
        
        // --- AUTO-CONFIGURATION LOGIC ---
        // The component subscribes to listen for when the world is ready.
        EventManager.getInstance().subscribe(EngineEvent.WORLD_LOADED, (data) -> {
            // Only configure if it hasn't been configured yet
            if (this.world == null && data instanceof WorldLoadedEventData) {
                WorldLoadedEventData eventData = (WorldLoadedEventData) data;
                this.world = eventData.world();
                this.allGameObjects = eventData.gameObjects();
                System.out.println("Movement component for '" + owner.name + "' configured via event!");
            }
        });
    }

    /**
     * Applies a guiding force to the current movement direction. This can be used
     * for effects like homing projectiles or gentle nudges.
     *
     * @param targetX The target X coordinate to move towards.
     * @param targetY The target Y coordinate to move towards.
     * @param strength A value between 0.0 and 1.0 indicating the strength of the guidance.
     *                 0.0 means no guidance, 1.0 means instantly moving towards the target.
     */
    public void applyGuidance(double targetX, double targetY, double strength) {
        if (owner == null) return;
        double currentMoveX = xRemainder;
        double currentMoveY = yRemainder;
        double idealDx = targetX - owner.getX();
        double idealDy = targetY - owner.getY();
        xRemainder = currentMoveX * (1.0 - strength) + idealDx * strength;
        yRemainder = currentMoveY * (1.0 - strength) + idealDy * strength;
    }
    
    /**
     * Sets the game world.
     * @param world The game world.
     */
    public void setWorld(World world) { this.world = world; }
    
    /**
     * Gets the game world.
     * @return The game world.
     */
    public World getWorld() { return this.world;}
    
    /**
     * Sets the list of game objects.
     * @param allGameObjects The list of game objects.
     */
    public void setGameObjects(List<GameObject> allGameObjects) { this.allGameObjects = allGameObjects; }

    /**
     * Abstract update method to be implemented by subclasses. This method
     * should contain the specific movement logic for the component.
     */
    public abstract void update();
    
	/**
     * Checks if the path is clear for the GameObject to move to the next position.
     * It checks for collisions with both world tiles and other GameObjects.
     *
     * @param nextX The next X coordinate.
     * @param nextY The next Y coordinate.
     * @return true if the path is clear, false otherwise.
     */
	protected boolean isPathClear(int nextX, int nextY) {
        if (world == null || owner == null) return true;

        // 1. Check for collision with world tiles
        if (!world.isFree(nextX, nextY, owner.getMaskX(), owner.getMaskY(), owner.getMaskWidth(), owner.getMaskHeight())) {
            return false;
        }

        // 2. Check for collision with other GameObjects
        if (allGameObjects != null) {
            Rectangle futureBounds = new Rectangle(
                nextX + owner.getMaskX(), 
                nextY + owner.getMaskY(), 
                owner.getMaskWidth(), 
                owner.getMaskHeight()
            );

            for (GameObject other : allGameObjects) {
                if (other == owner) continue;

                // Movement is blocked if the other object is SOLID or CHARACTER_SOLID.
                if (other.collisionType == CollisionType.SOLID || other.collisionType == CollisionType.CHARACTER_SOLID) {
                    
                    Rectangle otherBounds = new Rectangle(
                        other.getX() + other.getMaskX(),
                        other.getY() + other.getMaskY(),
                        other.getMaskWidth(),
                        other.getMaskHeight()
                    );
                    
                    if (futureBounds.intersects(otherBounds)) {
                        return false; // Block movement
                    }
                }
            }
        }
        
        return true; // Path is clear
    }
     
    /**
     * Gets the change in the X coordinate.
     * @return The change in the X coordinate.
     */
    public double getDx() { return dx; }
    
    /**
     * Gets the change in the Y coordinate.
     * @return The change in the Y coordinate.
     */
    public double getDy() { return dy; }
}
