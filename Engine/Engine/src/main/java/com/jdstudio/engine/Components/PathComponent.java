package com.jdstudio.engine.Components;

import java.awt.Point;
import java.util.List;

import com.jdstudio.engine.Object.GameObject;

/**
 * A helper class to manage movement along a predefined path for a GameObject.
 * It supports different patrol modes like looping and ping-pong.
 * Note: This is not a subclass of `Component` and is intended to be used by other components, such as `AIMovementComponent`.
 * 
 * @author JDStudio
 */
public class PathComponent {

    /**
     * Defines the behavior of the patrol path.
     */
    public enum PatrolMode {
        /** The owner loops back to the start of the path after reaching the end. */
        LOOP,
        /** The owner reverses direction upon reaching the end of the path. */
        PING_PONG
    }

    private final List<Point> path;
    private final PatrolMode mode;
    private final GameObject owner;
    private int currentTargetIndex = 0;
    private int direction = 1; // 1 for forward, -1 for backward
    private double arrivalThreshold = 15.0;

    /**
     * Constructs a new PathComponent.
     *
     * @param owner            The GameObject that will follow the path.
     * @param path             The list of points that make up the path.
     * @param mode             The patrol mode to use (LOOP or PING_PONG).
     * @param arrivalThreshold The distance at which the owner is considered to have reached a point.
     */
    public PathComponent(GameObject owner, List<Point> path, PatrolMode mode, Double arrivalThreshold) {
        this.owner = owner;
        this.path = path;
        this.mode = mode;
        this.arrivalThreshold = arrivalThreshold;
    }

    /**
     * Updates the path logic. If the owner has reached the current target point,
     * it advances to the next point in the path according to the patrol mode.
     */
    public void update() {
        if (path == null || path.isEmpty()) {
            return;
        }

        if (hasReachedTarget()) {
            advanceToNextPoint();
        }
    }

    /**
     * Gets the current target position in the path.
     *
     * @return The current target Point, or null if the path is invalid.
     */
    public Point getTargetPosition() {
        if (path == null || path.isEmpty() || currentTargetIndex >= path.size() || currentTargetIndex < 0) {
            return null;
        }
        return path.get(currentTargetIndex);
    }

    /**
     * Checks if the owner has reached the current target point.
     *
     * @return true if the distance to the target is within the arrival threshold.
     */
    private boolean hasReachedTarget() {
        Point target = getTargetPosition();
        if (target == null) return false;
        
        double ownerCenterX = owner.getX() + owner.getWidth() / 2.0;
        double ownerCenterY = owner.getY() + owner.getHeight() / 2.0;
        double dx = ownerCenterX - target.x;
        double dy = ownerCenterY - target.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        return distance < arrivalThreshold;
    }

    /**
     * Advances the target index to the next point in the path based on the patrol mode.
     */
    private void advanceToNextPoint() {
        if (mode == PatrolMode.LOOP) {
            currentTargetIndex = (currentTargetIndex + 1) % path.size();
        } else if (mode == PatrolMode.PING_PONG) {
            if (direction == 1 && (currentTargetIndex + direction) >= path.size()) {
                direction = -1; // Reverse direction at the end
            } else if (direction == -1 && (currentTargetIndex + direction) < 0) {
                direction = 1; // Reverse direction at the start
            }
            currentTargetIndex += direction;
        }
    }

    /**
     * Resets the path to its initial state, starting from the first point.
     */
    public void reset() {
        this.currentTargetIndex = 0;
        this.direction = 1;
    }

    /**
     * Gets the list of points that define the path.
     *
     * @return The list of path points.
     */
    public List<Point> getPath() {
        return this.path;
    }
}
