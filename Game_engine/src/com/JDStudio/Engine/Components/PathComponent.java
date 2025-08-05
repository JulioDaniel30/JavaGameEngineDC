// engine
package com.JDStudio.Engine.Components;

import java.awt.Point;
import java.util.List;

import com.JDStudio.Engine.Object.GameObject;

public class PathComponent {

    public enum PatrolMode {
        LOOP,
        PING_PONG
    }

    private final List<Point> path;
    private final PatrolMode mode;
    private final GameObject owner;
    private int currentTargetIndex = 0;
    private int direction = 1;

    private double arrivalThreshold = 15.0;


	public PathComponent(GameObject owner, List<Point> path, PatrolMode mode, Double arrivalThreshold) {
        this.owner = owner;
        this.path = path;
        this.mode = mode;
        this.arrivalThreshold = arrivalThreshold;
    }

    public void update() {
        if (path == null || path.isEmpty()) {
            return;
        }

        // --- INÍCIO DA DEPURAÇÃO ---
        //System.out.println("--- PathComponent Update Frame ---");
        //System.out.println("Owner: " + owner.name + " | Current Index: " + currentTargetIndex + " | Direction: " + direction);
        Point targetPos = getTargetPosition();
        //System.out.println("Current Target Position: " + targetPos);
        
        if (hasReachedTarget()) {
            //System.out.println(">>>>>> HAS REACHED TARGET! <<<<<<");
            advanceToNextPoint();
            //System.out.println("++++++ ADVANCED! New Index: " + currentTargetIndex + " | New Direction: " + direction);
            //System.out.println("++++++ New Target Position: " + getTargetPosition());
        } else {
            // Descomente a linha abaixo para ver a distância a cada quadro
            // System.out.println("Distance to target: " + getDistanceToTarget());
        }
        //System.out.println("------------------------------------");
        // --- FIM DA DEPURAÇÃO ---
    }
    
    // Método auxiliar para depuração
    private double getDistanceToTarget() {
        Point target = getTargetPosition();
        if (target == null) return -1;
        double ownerCenterX = owner.getX() + owner.getWidth() / 2.0;
        double ownerCenterY = owner.getY() + owner.getHeight() / 2.0;
        double dx = ownerCenterX - target.x;
        double dy = ownerCenterY - target.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public Point getTargetPosition() {
        if (path == null || path.isEmpty() || currentTargetIndex >= path.size() || currentTargetIndex < 0) {
            return null;
        }
        return path.get(currentTargetIndex);
    }

    private boolean hasReachedTarget() {
        return getDistanceToTarget() < arrivalThreshold;
    }

    private void advanceToNextPoint() {
        if (mode == PatrolMode.LOOP) {
            currentTargetIndex++;
            if (currentTargetIndex >= path.size()) {
                currentTargetIndex = 0;
            }
        }
        else if (mode == PatrolMode.PING_PONG) {
            if (direction == 1 && (currentTargetIndex + direction) >= path.size()) {
                direction = -1;
            }
            else if (direction == -1 && (currentTargetIndex + direction) < 0) {
                direction = 1;
            }
            currentTargetIndex += direction;
        }
    }

    public void reset() {
        this.currentTargetIndex = 0;
        this.direction = 1;
    }

    public List<Point> getPath() {
        return this.path;
    }
}