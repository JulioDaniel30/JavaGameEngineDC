// engine
package com.JDStudio.Engine.Components.Moviments;

import java.awt.Rectangle;
import java.util.List;
import com.JDStudio.Engine.Object.GameObject;
import com.JDStudio.Engine.Object.GameObject.CollisionType;
import com.JDStudio.Engine.World.World;

public abstract class BaseMovementComponent {

    protected GameObject owner;
    protected World world;
    protected List<GameObject> allGameObjects;
    public double speed;

    protected double xRemainder = 0.0;
    protected double yRemainder = 0.0;
    protected double dx = 0;
    protected double dy = 0;

    public BaseMovementComponent(GameObject owner, double speed) {
        this.owner = owner;
        this.speed = speed;
    }

    /**
     * Aplica uma "força" que ajusta a direção do movimento em direção a um ponto de guiamento.
     * @param targetX A coordenada X do ponto para o qual guiar.
     * @param targetY A coordenada Y do ponto para o qual guiar.
     * @param strength A força do guiamento (0.0 a 1.0).
     */
    public void applyGuidance(double targetX, double targetY, double strength) {
        // Pega a direção atual do movimento
        double currentMoveX = xRemainder;
        double currentMoveY = yRemainder;

        // Calcula a direção ideal (para o centro da porta)
        double idealDx = targetX - owner.getX();
        double idealDy = targetY - owner.getY();

        // Interpola linearmente entre o movimento atual e o ideal
        xRemainder = currentMoveX * (1.0 - strength) + idealDx * strength;
        yRemainder = currentMoveY * (1.0 - strength) + idealDy * strength;
    }
    
    public void setWorld(World world) { this.world = world; }
    public void setGameObjects(List<GameObject> allGameObjects) { this.allGameObjects = allGameObjects; }

    public abstract void tick();
    
    /**
     * Verifica se a próxima posição do objeto está livre,
     * considerando tanto os tiles do mundo quanto outros objetos sólidos.
     */
     protected boolean isPathClear(int nextX, int nextY) {
        if (world == null) return true;

        // 1. Verifica a colisão com os TILES do cenário (paredes)
        if (!world.isFree(nextX, nextY, owner.getMaskX(), owner.getMaskY(), owner.getMaskWidth(), owner.getMaskHeight())) {
            System.out.println(owner.name + " colidiu com um TILE sólido."); // Descomente para depuração
            return false;
        }

        // 2. Verifica a colisão com outros GAMEOBJECTS sólidos (portas, inimigos, etc.)
        if (allGameObjects != null) {
            Rectangle futureBounds = new Rectangle(
                nextX + owner.getMaskX(), 
                nextY + owner.getMaskY(), 
                owner.getMaskWidth(), 
                owner.getMaskHeight()
            );

            for (GameObject other : allGameObjects) {
                if (other == owner) continue; // Não colide consigo mesmo

                // A verificação crucial: o outro objeto é sólido?
                if (other.collisionType == CollisionType.SOLID) {
                    Rectangle otherBounds = new Rectangle(
                        other.getX() + other.getMaskX(),
                        other.getY() + other.getMaskY(),
                        other.getMaskWidth(),
                        other.getMaskHeight()
                    );
                    
                    // Se as caixas de colisão se intersetam, o caminho está bloqueado.
                    if (futureBounds.intersects(otherBounds)) {
                         System.out.println(owner.name + " colidiu com o GameObject sólido: " + other.name); // Descomente para depuração
                        return false;
                    }
                }
            }
        }
        
        // Se passou por todas as verificações, o caminho está livre.
        return true;
    }
     
     
     public double getDx() {
 		return dx;
 	}

 	public double getDy() {
 		return dy;
 	}
}