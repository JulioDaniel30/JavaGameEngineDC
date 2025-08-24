package com.jdstudio.engine.Components.Moviments;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import com.jdstudio.engine.Object.Character;
import com.jdstudio.engine.Object.GameObject;
import com.jdstudio.engine.Object.GameObject.CollisionType;
import com.jdstudio.engine.Pathfinding.Pathfinder;

/**
 * An AI-driven movement component that enables a GameObject to move towards a target.
 * It supports both direct movement and A* pathfinding.
 * The component can target a specific GameObject or a fixed point.
 * It also includes options for avoiding other actors and targeting specific anchor points on the target.
 * 
 * @author JDStudio
 */
public class AIMovementComponent extends BaseMovementComponent {

    /**
     * Defines the specific point on the target that the AI will pursue.
     */
    public enum TargetAnchor {
        /** The original (x, y) point of the target. */
        TOP_LEFT,
        /** The center of the target's sprite. */
        CENTER,
        /** The base of the target's sprite (the "feet"). */
        BOTTOM_CENTER
    }

    /** The GameObject to be pursued. */
    private GameObject target;
    
    /** A fixed point to move towards if no target GameObject is set. */
    private Point targetPoint;

    /** If true, the component will use the A* algorithm for pathfinding. */
    public boolean useAStarPathfinding = false;
    
    /** If true, the component will attempt to avoid other non-solid characters. */
    public boolean avoidOtherActors = false;
    
    /**
     * Defines which anchor to use when pursuing a target. Defaults to CENTER.
     */
    public TargetAnchor targetAnchor = TargetAnchor.CENTER;

    private List<Point> currentPath;
    private int currentPathIndex;
    private int pathRecalculateCooldown = 0;
    private final int pathRecalculateSpeed = 30; // Recalculate path every 30 frames
    private final double arrivalThreshold = 2.0; // Distance to consider as arrived at a waypoint

    /**
     * Constructs a new AIMovementComponent.
     *
     * @param speed The movement speed of the GameObject.
     */
    public AIMovementComponent(double speed) {
        super(speed);
        this.currentPath = new ArrayList<>();
    }

    /**
     * Sets the target for the AI to move towards.
     *
     * @param target The GameObject to target.
     */
    public void setTarget(GameObject target) {
        if (this.target != target) {
            this.target = target;
            this.targetPoint = null; // Clear fixed point target
            this.pathRecalculateCooldown = 0; // Force path recalculation
            this.currentPath.clear();
        }
    }
    
    /**
     * Gets the current GameObject target.
     *
     * @return The current target, or null if none is set.
     */
    public GameObject getTarget() {
        return this.target;
    }

    /**
     * Sets a fixed point for the AI to move towards.
     *
     * @param x The target x-coordinate.
     * @param y The target y-coordinate.
     */
    public void setTarget(int x, int y) {
        this.target = null; // Clear GameObject target
        this.targetPoint = new Point(x, y);
        this.pathRecalculateCooldown = 0; // Force path recalculation
        this.currentPath.clear();
    }
    
    /**
     * Updates the AI's movement logic for the current frame.
     * It determines the target position and moves the owner GameObject accordingly.
     */
    @Override
    public void update() {
        if (target == null && targetPoint == null) {
            dx = 0;
            dy = 0;
            return;
        }

        Point finalTarget = getFinalTarget();
        if (finalTarget == null) return;
        
        smartMoveTowards(finalTarget.x, finalTarget.y);
    }

    /**
     * Determines the final target point for the current frame.
     * If A* pathfinding is enabled, it calculates and follows the path.
     * Otherwise, it returns the direct position of the target based on the anchor.
     *
     * @return The Point to move towards, or null if no path is available.
     */
    private Point getFinalTarget() {
        if (useAStarPathfinding && (target != null || targetPoint != null)) {
            pathRecalculateCooldown--;
            if (pathRecalculateCooldown <= 0 || currentPath == null || currentPath.isEmpty()) {
                pathRecalculateCooldown = pathRecalculateSpeed;
                Point startPoint = new Point(owner.getX(), owner.getY());
                Point endPoint = (target != null) ? getAnchorPoint(target) : targetPoint;

                if (endPoint == null) return null;
                
                this.currentPath = Pathfinder.findPath(world, startPoint, endPoint);
                this.currentPathIndex = 0;
            }

            if (currentPath == null || currentPath.isEmpty()) return null;

            Point nextWaypoint = currentPath.get(currentPathIndex);
            
            // Check if we have arrived at the current waypoint
            double dxToWaypoint = nextWaypoint.x - (owner.getX() + owner.getWidth() / 2.0);
            double dyToWaypoint = nextWaypoint.y - (owner.getY() + owner.getHeight() / 2.0);
            if (Math.sqrt(dxToWaypoint * dxToWaypoint + dyToWaypoint * dyToWaypoint) < arrivalThreshold) {
                currentPathIndex++;
                if (currentPathIndex >= currentPath.size()) {
                    currentPath.clear(); // Path completed
                    return null; 
                }
            }
            return currentPath.get(currentPathIndex);

        } else if (target != null) {
            return getAnchorPoint(target);
        } else {
            return targetPoint;
        }
    }
    
    /**
     * Calculates the exact target coordinate based on the selected anchor.
     *
     * @param targetObject The target GameObject.
     * @return A Point with the calculated target coordinates.
     */
    private Point getAnchorPoint(GameObject targetObject) {
        if (targetObject == null) {
            return null;
        }
        
        int targetX, targetY;

        switch (this.targetAnchor) {
            case TOP_LEFT:
                targetX = targetObject.getX();
                targetY = targetObject.getY();
                break;
            
            case BOTTOM_CENTER:
                targetX = targetObject.getX() + targetObject.getWidth() / 2;
                targetY = targetObject.getY() + targetObject.getHeight() - 1;
                break;

            case CENTER:
            default:
                targetX = targetObject.getX() + targetObject.getWidth() / 2;
                targetY = targetObject.getY() + targetObject.getHeight() / 2;
                break;
        }
        return new Point(targetX, targetY);
    }

    /**
     * Calculates the movement vector towards the target and applies it.
     *
     * @param targetX The final target x-coordinate.
     * @param targetY The final target y-coordinate.
     */
    private void smartMoveTowards(double targetX, double targetY) {
        double startX = owner.getX() + owner.getWidth() / 2.0;
        double startY = owner.getY() + owner.getHeight() / 2.0;

        double dx = targetX - startX;
        double dy = targetY - startY;
        double length = Math.sqrt(dx * dx + dy * dy);

        if (length < arrivalThreshold) {
            this.dx = 0;
            this.dy = 0;
            return;
        }

        this.dx = (dx / length) * speed;
        this.dy = (dy / length) * speed;
        
        applyIntelligentMovement(this.dx, this.dy);
    }

    /**
     * Applies the calculated movement to the owner, handling sub-pixel movement
     * and basic collision avoidance to prevent getting stuck.
     *
     * @param moveX The calculated movement on the X axis.
     * @param moveY The calculated movement on the Y axis.
     */
    private void applyIntelligentMovement(double moveX, double moveY) {
        xRemainder += moveX;
        yRemainder += moveY;

        int xToMove = (int) xRemainder;
        int yToMove = (int) yRemainder;

        xRemainder -= xToMove;
        yRemainder -= yToMove;
        
        int signX = Integer.signum(xToMove);
        int signY = Integer.signum(yToMove);

        // Simple sliding logic: if diagonal movement is blocked, try moving on one axis.
        if (xToMove != 0 && yToMove != 0) {
            if (!isPathClear(owner.getX() + signX, owner.getY()) || 
                !isPathClear(owner.getX(), owner.getY() + signY)) 
            {
                if (Math.abs(moveX) > Math.abs(moveY)) {
                    yToMove = 0;
                } else {
                    xToMove = 0;
                }
            }
        }

        // Move on X axis
        if (xToMove != 0) {
            for (int i = 0; i < Math.abs(xToMove); i++) {
                if (isPathClear(owner.getX() + signX, owner.getY())) {
                    owner.setX(owner.getX() + signX);
                } else {
                    xRemainder = 0; 
                    break;
                }
            }
        }

        // Move on Y axis
        if (yToMove != 0) {
            for (int i = 0; i < Math.abs(yToMove); i++) {
                if (isPathClear(owner.getX(), owner.getY() + signY)) {
                    owner.setY(owner.getY() + signY);
                } else {
                    yRemainder = 0;
                    break;
                }
            }
        }
    }
    
    /**
     * Extends the base collision check to optionally avoid other non-solid actors.
     * This is useful for preventing AI characters from clumping together.
     *
     * @param nextX The next X coordinate to check.
     * @param nextY The next Y coordinate to check.
     * @return false if the path is blocked, true otherwise.
     */
    @Override
    protected boolean isPathClear(int nextX, int nextY) {
        if (!super.isPathClear(nextX, nextY)) {
            return false;
        }

        if (avoidOtherActors) {
            Rectangle futureBounds = new Rectangle(
                nextX + owner.getMaskX(), 
                nextY + owner.getMaskY(), 
                owner.getMaskWidth(), 
                owner.getMaskHeight()
            );

            for (GameObject other : allGameObjects) {
                if (other == owner || other == target) continue;

                // Check against other non-solid characters
                if (other instanceof Character && other.getCollisionType() != CollisionType.SOLID) {
                    Rectangle otherBounds = new Rectangle(
                        other.getX() + other.getMaskX(),
                        other.getY() + other.getMaskY(),
                        other.getMaskWidth(),
                        other.getMaskHeight()
                    );
                    if (futureBounds.intersects(otherBounds)) {
                        return false; // Path is blocked by another actor
                    }
                }
            }
        }
        
        return true;
    }
}
