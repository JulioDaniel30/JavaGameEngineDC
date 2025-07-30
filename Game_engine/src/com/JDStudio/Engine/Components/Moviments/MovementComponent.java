// engine
package com.JDStudio.Engine.Components.Moviments;

import com.JDStudio.Engine.Object.GameObject;

public class MovementComponent extends BaseMovementComponent {

   // private double dx = 0, dy = 0;

    public MovementComponent(GameObject owner, double speed) {
        super(owner, speed);
    }
    
    /**
     * Define a direção do movimento para o próximo tick. Usado para controle direto (jogador).
     * @param dx Componente X da direção (-1, 0, ou 1)
     * @param dy Componente Y da direção (-1, 0, ou 1)
     */
    public void setDirection(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void tick() {
        if (dx == 0 && dy == 0) {
            xRemainder = 0;
            yRemainder = 0;
            return;
        }

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