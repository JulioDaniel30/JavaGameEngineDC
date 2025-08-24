package com.jdstudio.engine.Components.Moviments;

/**
 * A component for direct, non-AI movement, typically controlled by player input.
 * It moves the GameObject based on a direction vector and speed, performing
 * collision checks with the world and other solid objects.
 * 
 * @author JDStudio
 */
public class MovementComponent extends BaseMovementComponent {

    /**
     * Constructs a new MovementComponent.
     *
     * @param speed The movement speed for the GameObject.
     */
    public MovementComponent(double speed) {
        super(speed);
    }
    
    /**
     * Sets the direction of movement for the next update tick. 
     * This is typically used for direct control, such as from player input.
     * The vector will be normalized before being used.
     *
     * @param dx The X component of the direction (e.g., -1 for left, 1 for right).
     * @param dy The Y component of the direction (e.g., -1 for up, 1 for down).
     */
    public void setDirection(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Updates the position of the GameObject based on the current direction and speed.
     * It handles sub-pixel movement and collision detection, moving the object
     * pixel by pixel to avoid passing through obstacles.
     */
    @Override
    public void update() {
        if (dx == 0 && dy == 0) {
            // No movement, reset remainders
            xRemainder = 0;
            yRemainder = 0;
            return;
        }

        double moveX = dx;
        double moveY = dy;
        
        // Normalize the direction vector to ensure consistent speed
        double length = Math.sqrt(moveX * moveX + moveY * moveY);
        if (length > 0) {
            moveX = (moveX / length);
            moveY = (moveY / length);
        }
        
        // Apply speed to the normalized vector
        moveX *= speed;
        moveY *= speed;

        // Add the movement to the sub-pixel remainders
        xRemainder += moveX;
        yRemainder += moveY;

        // Determine the integer part of the movement for this frame
        int xToMove = (int) xRemainder;
        int yToMove = (int) yRemainder;

        // Subtract the integer part, keeping the fractional part for the next frame
        xRemainder -= xToMove;
        yRemainder -= yToMove;

        // Move one pixel at a time on the X axis, checking for collisions
        if (xToMove != 0) {
            int signX = Integer.signum(xToMove);
            for (int i = 0; i < Math.abs(xToMove); i++) {
                if (isPathClear(owner.getX() + signX, owner.getY())) {
                    owner.setX(owner.getX() + signX);
                } else {
                    xRemainder = 0; // Stop movement and clear remainder if blocked
                    break;
                }
            }
        }

        // Move one pixel at a time on the Y axis, checking for collisions
        if (yToMove != 0) {
            int signY = Integer.signum(yToMove);
            for (int i = 0; i < Math.abs(yToMove); i++) {
                if (isPathClear(owner.getX(), owner.getY() + signY)) {
                    owner.setY(owner.getY() + signY);
                } else {
                    yRemainder = 0; // Stop movement and clear remainder if blocked
                    break;
                }
            }
        }
    }
}
