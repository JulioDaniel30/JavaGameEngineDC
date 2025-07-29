// engine
package com.JDStudio.Engine.Components.Moviments;

import java.awt.Rectangle;
import com.JDStudio.Engine.Object.Character; // Importação necessária
import com.JDStudio.Engine.Object.GameObject;

public class AIMovementComponent extends BaseMovementComponent {

    private GameObject target;

    /** Se true, o componente tentará evitar colidir com outros Characters. */
    public boolean avoidOtherActors = false;

    public AIMovementComponent(GameObject owner, double speed) {
        super(owner, speed);
    }

    public void setTarget(GameObject target) {
        this.target = target;
    }

    /**
     * Sobrescreve o método isPathClear para adicionar a lógica de evitar outros atores.
     */
    @Override
    protected boolean isPathClear(int nextX, int nextY) {
        // 1. A verificação original contra tiles e objetos sólidos continua a mesma.
        if (!super.isPathClear(nextX, nextY)) {
            return false;
        }

        // 2. NOVA VERIFICAÇÃO: Se a opção estiver ativa, verifica contra outros Characters.
        if (avoidOtherActors) {
            Rectangle futureBounds = new Rectangle(
                nextX + owner.getMaskX(), 
                nextY + owner.getMaskY(), 
                owner.getMaskWidth(), 
                owner.getMaskHeight()
            );

            for (GameObject other : allGameObjects) {
                // Não verifica contra si mesmo ou contra o alvo que está perseguindo
                if (other == owner || other == target) continue;

                // Verifica se o outro objeto é um "Character" (jogador, outro inimigo, etc.)
                // e não é um objeto sólido (como uma porta, que já foi verificado no super.isPathClear)
                if (other instanceof Character && !other.isSolid) {
                    Rectangle otherBounds = new Rectangle(
                        other.getX() + other.getMaskX(),
                        other.getY() + other.getMaskY(),
                        other.getMaskWidth(),
                        other.getMaskHeight()
                    );
                    if (futureBounds.intersects(otherBounds)) {
                        return false; // Colidiria com outro ator, caminho bloqueado!
                    }
                }
            }
        }
        
        return true; // Caminho totalmente livre
    }

    @Override
    public void tick() {
        if (target == null) {
            xRemainder = 0;
            yRemainder = 0;
            return;
        }

        double dx = target.getX() - owner.getX();
        double dy = target.getY() - owner.getY();

        double moveX = dx;
        double moveY = dy;
        double length = Math.sqrt(moveX * moveX + moveY * moveY);
        if (length > 0) {
            moveX = (moveX / length);
            moveY = (moveY / length);
        }
        
        moveX *= speed;
        moveY *= speed;

        xRemainder += moveX;
        yRemainder += moveY;

        int xToMove = (int) xRemainder;
        int yToMove = (int) yRemainder;

        xRemainder -= xToMove;
        yRemainder -= yToMove;

        if (xToMove != 0) {
            int signX = Integer.signum(xToMove);
            for (int i = 0; i < Math.abs(xToMove); i++) {
                if (isPathClear(owner.getX() + signX, owner.getY())) {
                    owner.setX(owner.getX() + signX);
                } else {
                    xRemainder = 0; 
                    break;
                }
            }
        }

        if (yToMove != 0) {
            int signY = Integer.signum(yToMove);
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
}