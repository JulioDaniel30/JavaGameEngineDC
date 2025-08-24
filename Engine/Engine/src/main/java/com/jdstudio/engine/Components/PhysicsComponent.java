package com.jdstudio.engine.Components;

import com.jdstudio.engine.Events.EngineEvent;
import com.jdstudio.engine.Events.EventManager;
import com.jdstudio.engine.Events.WorldLoadedEventData;
import com.jdstudio.engine.World.World;

/**
 * A component that adds physics simulation to a GameObject.
 * It handles gravity, velocity, and acceleration, and performs collision detection with the world.
 * The component automatically subscribes to the WORLD_LOADED event to get a reference to the world.
 * 
 * @author JDStudio
 */
public class PhysicsComponent extends Component {

    // --- Physics Properties ---
    /** The horizontal velocity of the object. */
    public double velocityX;
    /** The vertical velocity of the object. */
    public double velocityY;
    /** The horizontal acceleration of the object. */
    public double accelerationX;
    /** The vertical acceleration of the object. */
    public double accelerationY;

    /** The strength of gravity to apply to the object. */
    public double gravity = 0.5;
    /** Flag indicating if the object is currently on the ground. */
    public boolean onGround = false;

    /** A reference to the world, obtained via event. */
    private World world;

    /**
     * The constructor is responsible for subscribing to the WORLD_LOADED event.
     */
    public PhysicsComponent() {
        // The component subscribes to listen for when the world is ready.
        EventManager.getInstance().subscribe(EngineEvent.WORLD_LOADED, (data) -> {
            // Only configure if it hasn't been configured yet
            if (this.world == null && data instanceof WorldLoadedEventData) {
                WorldLoadedEventData eventData = (WorldLoadedEventData) data;
                this.world = eventData.world();
                System.out.println("PhysicsComponent for '" + owner.name + "' configured via event!");
            }
        });
    }

    /**
     * Updates the physics simulation for the object.
     * It applies acceleration, gravity, and then moves the object, checking for collisions.
     */
    @Override
    public void update() {
        // If the world hasn't been loaded yet, do nothing to avoid errors.
        if (world == null) return;
        owner.onGround = false;

        // 1. Apply acceleration to velocity
        this.velocityX += this.accelerationX;
        this.velocityY += this.accelerationY;

        // 2. Apply gravity to vertical velocity
        this.velocityY += this.gravity;

        // 3. Move the object on the X-axis and check for collisions
        moveAndCollide(velocityX, 0);

        // 4. Move the object on the Y-axis and check for collisions
        moveAndCollide(0, velocityY);

        owner.velocityY = this.velocityY;
        // Reset acceleration each frame
        this.accelerationX = 0;
        this.accelerationY = 0;
    }

    /**
     * Moves the object and responds to collisions, one axis at a time.
     * This ensures accurate collision detection by preventing corner-snagging.
     * 
     * @param moveX The movement to apply on the X-axis.
     * @param moveY The movement to apply on the Y-axis.
     */
    private void moveAndCollide(double moveX, double moveY) {
        this.onGround = false; // Assume not on the ground until proven otherwise

        // Move pixel by pixel for precise collision
        for (int i = 0; i < Math.abs(moveX); i++) {
            if (!isPathClearForPhysics(owner.getX() + (int)Math.signum(moveX), owner.getY())) {
                this.velocityX = 0; // Hit a wall, stop horizontal velocity
                break;
            }
            owner.setX(owner.getX() + (int)Math.signum(moveX));
        }

        for (int i = 0; i < Math.abs(moveY); i++) {
            if (!isPathClearForPhysics(owner.getX(), owner.getY() + (int)Math.signum(moveY))) {
                // If it was falling, it means it has landed
                if (moveY > 0) {
                    this.onGround = true;
                }
                this.velocityY = 0; // Hit the floor or ceiling, stop vertical velocity
                break;
            }
            owner.setY(owner.getY() + (int)Math.signum(moveY));
        }
    }

    /**
     * Checks if the path is clear for a physics-enabled object.
     * Uses the isFree method from the World.
     * 
     * @param nextX The next X coordinate to check.
     * @param nextY The next Y coordinate to check.
     * @return true if the path is clear, false otherwise.
     */
    private boolean isPathClearForPhysics(int nextX, int nextY) {
        // The 'world == null' check already happens at the beginning of update()
        return world.isFree(nextX, nextY, owner.getMaskX(), owner.getMaskY(), owner.getMaskWidth(), owner.getMaskHeight());
    }
    
    /**
     * Applies an instantaneous vertical force to the object (e.g., a jump).
     * 
     * @param force The force of the jump (a negative value to go up).
     */
    public void addVerticalForce(double force) {
        this.velocityY = force;
    }
}
