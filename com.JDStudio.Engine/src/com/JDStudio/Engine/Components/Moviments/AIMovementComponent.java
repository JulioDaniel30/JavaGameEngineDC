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

    // --- NOVO ENUM PARA A ÂNCORA ---
    /**
     * Define o ponto específico no alvo que a IA irá perseguir.
     */
    public enum TargetAnchor {
        TOP_LEFT,      // O ponto (x, y) original.
        CENTER,        // O centro do sprite do alvo.
        BOTTOM_CENTER  // A base do sprite do alvo (os "pés").
    }

    private GameObject target;
    private Point targetPoint;

    public boolean useAStarPathfinding = false;
    public boolean avoidOtherActors = false;
    
    // --- NOVA PROPRIEDADE CONFIGURÁVEL ---
    /**
     * Define qual âncora usar ao perseguir um alvo. O padrão é o centro.
     */
    public TargetAnchor targetAnchor = TargetAnchor.CENTER;

    private List<Point> currentPath;
    private int currentPathIndex;
    private int pathRecalculateCooldown = 0;
    private final int pathRecalculateSpeed = 30;
    private final double arrivalThreshold = 2.0;

    public AIMovementComponent(double speed) {
        super(speed);
        this.currentPath = new ArrayList<>();
    }
    
    // ... (métodos setTarget, getTarget, etc. permanecem iguais) ...

    public void setTarget(GameObject target) {
        if (this.target != target) {
            this.target = target;
            this.pathRecalculateCooldown = 0;
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
    public void update() {
    	
    	    
        if (target == null && targetPoint == null) {
            xRemainder = 0;
            yRemainder = 0;
            return;
        }

        Point finalTarget = getFinalTarget();
        if (finalTarget == null) return;
        
        smartMoveTowards(finalTarget.x, finalTarget.y);
    }

    private Point getFinalTarget() {
        if (useAStarPathfinding && target != null) {
            pathRecalculateCooldown--;
            if (pathRecalculateCooldown <= 0 || currentPath == null || currentPath.isEmpty()) {
                pathRecalculateCooldown = pathRecalculateSpeed;
                Point startPoint = new Point(owner.getX(), owner.getY());
                
                // --- MUDANÇA AQUI: Usa o método auxiliar para pegar o ponto da âncora ---
                Point endPoint = getAnchorPoint(target);
                if (endPoint == null) return null;
                
                this.currentPath = Pathfinder.findPath(world, startPoint, endPoint);
                this.currentPathIndex = 0;
            }

            if (currentPath == null || currentPath.isEmpty()) return null;

            Point nextWaypoint = currentPath.get(currentPathIndex);
            
            double dxToWaypoint = nextWaypoint.x - (owner.getX() + owner.getWidth() / 2.0);
            double dyToWaypoint = nextWaypoint.y - (owner.getY() + owner.getHeight() / 2.0);
            if (Math.sqrt(dxToWaypoint * dxToWaypoint + dyToWaypoint * dyToWaypoint) < arrivalThreshold) {
                currentPathIndex++;
                if (currentPathIndex >= currentPath.size()) {
                    currentPath.clear();
                    return null; 
                }
            }
            return currentPath.get(currentPathIndex);

        } else if (target != null) {
            // --- MUDANÇA AQUI: Também usa a âncora para movimento direto ---
            return getAnchorPoint(target);
        } else {
            return targetPoint;
        }
    }
    
    // --- NOVO MÉTODO AUXILIAR ---
    /**
     * Calcula a coordenada exata do alvo com base na âncora selecionada.
     * @param targetObject O GameObject alvo.
     * @return Um Point com a coordenada do alvo.
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
                // --- CORREÇÃO AQUI ---
                // Usamos getHeight() - 1 para garantir que o ponto esteja na ÚLTIMA
                // linha de pixels do sprite, e não fora dele.
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

    // ... (O resto da classe: smartMoveTowards, applyIntelligentMovement, isPathClear, etc. permanece o mesmo) ...
    private void smartMoveTowards(double targetX, double targetY) {
        // --- CORREÇÃO FINAL AQUI ---
        // O ponto de partida do movimento AGORA é o CENTRO do inimigo, para ser consistente.
        double startX = owner.getX() + owner.getWidth() / 2.0;
        double startY = owner.getY() + owner.getHeight() / 2.0;

        // O resto do cálculo agora usa o ponto de partida correto
        double dx = targetX - startX;
        double dy = targetY - startY;
        double length = Math.sqrt(dx * dx + dy * dy);

        if (length < arrivalThreshold) return;

        double moveX = (dx / length) * speed;
        double moveY = (dy / length) * speed;
        
        applyIntelligentMovement(moveX, moveY);
    }

    private void applyIntelligentMovement(double moveX, double moveY) {
        xRemainder += moveX;
        yRemainder += moveY;

        int xToMove = (int) xRemainder;
        int yToMove = (int) yRemainder;

        xRemainder -= xToMove;
        yRemainder -= yToMove;
        
        int signX = Integer.signum(xToMove);
        int signY = Integer.signum(yToMove);

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

                if (other instanceof Character && other.getCollisionType() != CollisionType.SOLID) {
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