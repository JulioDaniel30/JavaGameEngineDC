// engine
package com.jdstudio.engine.Components.Moviments;

import java.awt.Rectangle;
import java.util.List;

import com.jdstudio.engine.Components.Component;
import com.jdstudio.engine.Events.EngineEvent;
import com.jdstudio.engine.Events.EventManager;
import com.jdstudio.engine.Events.WorldLoadedEventData;
import com.jdstudio.engine.Object.GameObject;
import com.jdstudio.engine.Object.GameObject.CollisionType;
import com.jdstudio.engine.World.World;

public abstract class BaseMovementComponent extends Component {

    // A linha "protected GameObject owner;" foi REMOVIDA daqui.
    // Agora, este componente usará a variável "owner" herdada da classe Component, que é a correta.
    
    protected World world;
    protected List<GameObject> allGameObjects;
    public double speed;

    protected double xRemainder = 0.0;
    protected double yRemainder = 0.0;
    protected double dx = 0;
    protected double dy = 0;

    public BaseMovementComponent(double speed) {
        this.speed = speed;
        
        // --- LÓGICA DE AUTO-CONFIGURAÇÃO ---
        // O componente se inscreve para ouvir quando o mundo estiver pronto.
        EventManager.getInstance().subscribe(EngineEvent.WORLD_LOADED, (data) -> {
            // Apenas configura se ainda não tiver sido configurado
            if (this.world == null && data instanceof WorldLoadedEventData) {
                WorldLoadedEventData eventData = (WorldLoadedEventData) data;
                this.world = eventData.world();
                this.allGameObjects = eventData.gameObjects();
                System.out.println("Componente de movimento para '" + owner.name + "' configurado via evento!");
            }
        });
    }

    public void applyGuidance(double targetX, double targetY, double strength) {
        if (owner == null) return;
        double currentMoveX = xRemainder;
        double currentMoveY = yRemainder;
        double idealDx = targetX - owner.getX();
        double idealDy = targetY - owner.getY();
        xRemainder = currentMoveX * (1.0 - strength) + idealDx * strength;
        yRemainder = currentMoveY * (1.0 - strength) + idealDy * strength;
    }
    
    public void setWorld(World world) { this.world = world; }
    public World getWorld() { return this.world;}
    public void setGameObjects(List<GameObject> allGameObjects) { this.allGameObjects = allGameObjects; }

    public abstract void update();
    
	protected boolean isPathClear(int nextX, int nextY) {
        if (world == null || owner == null) return true;

        // 1. Verifica colisão com os tiles do mundo (inalterado)
        if (!world.isFree(nextX, nextY, owner.getMaskX(), owner.getMaskY(), owner.getMaskWidth(), owner.getMaskHeight())) {
            return false;
        }

        // 2. Verifica colisão com outros GameObjects
        if (allGameObjects != null) {
            Rectangle futureBounds = new Rectangle(
                nextX + owner.getMaskX(), 
                nextY + owner.getMaskY(), 
                owner.getMaskWidth(), 
                owner.getMaskHeight()
            );

            for (GameObject other : allGameObjects) {
                if (other == owner) continue;

                // --- CORREÇÃO PRINCIPAL E DESACOPLADA AQUI ---
                // O movimento é bloqueado se o outro objeto for SOLID ou CHARACTER_SOLID.
                // Esta regra é 100% genérica e não depende do jogo.
                if (other.collisionType == CollisionType.SOLID || other.collisionType == CollisionType.CHARACTER_SOLID) {
                    
                    Rectangle otherBounds = new Rectangle(
                        other.getX() + other.getMaskX(),
                        other.getY() + other.getMaskY(),
                        other.getMaskWidth(),
                        other.getMaskHeight()
                    );
                    
                    if (futureBounds.intersects(otherBounds)) {
                        return false; // Bloqueia o movimento
                    }
                }
            }
        }
        
        return true; // Caminho livre
    }
     
    public double getDx() { return dx; }
    public double getDy() { return dy; }
}