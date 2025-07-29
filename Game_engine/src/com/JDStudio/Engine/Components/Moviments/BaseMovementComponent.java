// engine
package com.JDStudio.Engine.Components.Moviments;

import java.awt.Rectangle;
import java.util.List;
import com.JDStudio.Engine.Object.GameObject;
import com.JDStudio.Engine.World.World;

public abstract class BaseMovementComponent {

    protected GameObject owner;
    protected World world;
    protected List<GameObject> allGameObjects;
    public double speed;

    protected double xRemainder = 0.0;
    protected double yRemainder = 0.0;

    public BaseMovementComponent(GameObject owner, double speed) {
        this.owner = owner;
        this.speed = speed;
    }

    public void setWorld(World world) { this.world = world; }
    public void setGameObjects(List<GameObject> allGameObjects) { this.allGameObjects = allGameObjects; }

    /**
     * O método principal que deve ser chamado a cada frame pelo GameObject.
     * A implementação será específica para cada tipo de movimento.
     */
    public abstract void tick();
    
    /**
     * Verifica se a próxima posição do objeto está livre.
     */
    protected boolean isPathClear(int nextX, int nextY) {
        if (world == null) return true; // Se não há mundo, não há colisão

        if (!world.isFree(nextX, nextY, owner.getMaskX(), owner.getMaskY(), owner.getMaskWidth(), owner.getMaskHeight())) {
            return false;
        }

        if (allGameObjects != null) {
            Rectangle futureBounds = new Rectangle(
                nextX + owner.getMaskX(), 
                nextY + owner.getMaskY(), 
                owner.getMaskWidth(), 
                owner.getMaskHeight()
            );

            for (GameObject other : allGameObjects) {
                if (other == owner) continue;
                if (other.isSolid) {
                    Rectangle otherBounds = new Rectangle(
                        other.getX() + other.getMaskX(),
                        other.getY() + other.getMaskY(),
                        other.getMaskWidth(),
                        other.getMaskHeight()
                    );
                    if (futureBounds.intersects(otherBounds)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}