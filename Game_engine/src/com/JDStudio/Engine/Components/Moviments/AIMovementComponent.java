// engine
package com.JDStudio.Engine.Components.Moviments;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import com.JDStudio.Engine.Object.Character;
import com.JDStudio.Engine.Object.GameObject;
import com.JDStudio.Engine.Object.GameObject.CollisionType;
import com.JDStudio.Engine.Pathfinding.Pathfinder;

public class AIMovementComponent extends BaseMovementComponent {

    private GameObject target;
    private Point targetPoint; // Usado para patrulha (movimento direto)

    // --- OPÇÃO CONFIGURÁVEL ---
    /** * Se true, usará o algoritmo A* para desviar de obstáculos ao seguir um GameObject.
     * Se false, moverá em linha reta em direção ao alvo.
     */
    public boolean useAStarPathfinding = false;
    public boolean avoidOtherActors = false;

    // Atributos para o A*
    private List<Point> currentPath;
    private int currentPathIndex;
    private int pathRecalculateCooldown = 0;
    private final int pathRecalculateSpeed = 30; // Recalcula o caminho a cada 30 ticks (meio segundo)
    private final double arrivalThreshold = 5.0; // Distância para considerar que chegou a um ponto do caminho A*
    

    public AIMovementComponent(GameObject owner, double speed) {
        super(owner, speed);
        this.currentPath = new ArrayList<>();
    }

    public void setTarget(GameObject target) {
        if (this.target != target) {
            this.target = target;
            this.pathRecalculateCooldown = 0; // Força recálculo imediato do caminho
            this.currentPath.clear();
        }
    }
    
    public GameObject getTarget() {
    	return this.target;
    }

    public void setTarget(int x, int y) {
        this.target = null;
        this.targetPoint = new Point(x, y);
    }

    @Override
    public void tick() {
        // Se não há alvo de nenhum tipo, para.
        if (target == null && targetPoint == null) {
            xRemainder = 0;
            yRemainder = 0;
            return;
        }

        // Decide qual lógica de movimento usar
        if (useAStarPathfinding && target != null) {
            tickAStar();
        } else {
            tickDirectMovement();
        }
    }

    /**
     * Lógica de movimento usando A* Pathfinding para navegar por obstáculos.
     */
    private void tickAStar() {
        pathRecalculateCooldown--;
        if (pathRecalculateCooldown <= 0) {
            pathRecalculateCooldown = pathRecalculateSpeed;
            Point startPoint = new Point(owner.getX(), owner.getY());
            Point endPoint = new Point(target.getX(), target.getY());
            
            // Pede à engine para encontrar um novo caminho
            this.currentPath = Pathfinder.findPath(world, startPoint, endPoint);
            this.currentPathIndex = 0;
        }

        if (currentPath == null || currentPath.isEmpty()) {
            // Se não há caminho (ex: alvo inalcançável), não se move
            return;
        }

        // Define o próximo ponto do caminho como o alvo imediato
        Point currentTargetPoint = currentPath.get(currentPathIndex);
        
        // Verifica se chegou ao ponto atual do caminho
        double dxToPoint = currentTargetPoint.x - owner.getX();
        double dyToPoint = currentTargetPoint.y - owner.getY();
        if (Math.sqrt(dxToPoint * dxToPoint + dyToPoint * dyToPoint) < arrivalThreshold) {
            currentPathIndex++; // Se chegou, avança para o próximo ponto
            if (currentPathIndex >= currentPath.size()) {
                currentPath.clear(); // Chegou ao fim do caminho
                return;
            }
        }
        
        // Move em direção ao ponto atual do caminho A*
        moveTowardsPoint(currentTargetPoint.x, currentTargetPoint.y);
    }

    /**
     * Lógica de movimento simples e direta, em linha reta.
     */
    private void tickDirectMovement() {
        double targetX, targetY;
        if (target != null) {
            targetX = target.getX();
            targetY = target.getY();
        } else {
            targetX = targetPoint.x;
            targetY = targetPoint.y;
        }
        moveTowardsPoint(targetX, targetY);
    }

    /**
     * Lógica de baixo nível que move o objeto pixel por pixel em direção a um ponto.
     * Esta função é usada tanto pelo A* quanto pelo movimento direto.
     */
    private void moveTowardsPoint(double targetX, double targetY) {
        double dx = targetX - owner.getX();
        double dy = targetY - owner.getY();
        this.dx = dx;
        this.dy = dy;
        double moveX = dx;
        double moveY = dy;
        double length = Math.sqrt(moveX * moveX + moveY * moveY);
        
        // Evita movimento se já estiver muito perto (para patrulha)
        if (length < 1.0) return;

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

    @Override
    protected boolean isPathClear(int nextX, int nextY) {
        // A primeira verificação (super.isPathClear) já lida com os tiles
        // e com TODOS os GameObjects marcados como 'isSolid = true' (como a porta fechada).
        if (!super.isPathClear(nextX, nextY)) {
            return false;
        }

        // A segunda verificação, se ativa, lida com a colisão entre ATORES (Characters)
        // que não são necessariamente sólidos, para que não se sobreponham.
        if (avoidOtherActors) {
            Rectangle futureBounds = new Rectangle(
                nextX + owner.getMaskX(), 
                nextY + owner.getMaskY(), 
                owner.getMaskWidth(), 
                owner.getMaskHeight()
            );

            for (GameObject other : allGameObjects) {
                // Não verifica contra si mesmo ou contra o alvo que está a perseguir (para não ficar preso)
                if (other == owner || other == target) continue;

                // Verifica se o outro objeto é um "Character" e não é um objeto sólido
                // (pois os sólidos já foram verificados pelo super.isPathClear)
                if (other instanceof Character && other.getCollisionType() != CollisionType.SOLID) {
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

	
    
    
    
    
}