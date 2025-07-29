// game
package com.game;

import com.JDStudio.Engine.Components.Moviments.AIMovementComponent;
import com.JDStudio.Engine.Graphics.Sprite.Sprite;
import com.JDStudio.Engine.Object.Character;
import com.JDStudio.Engine.Object.GameObject;

public class Enemy extends Character {

    private final int visionRadius = 80;
    private final int attackRadius = 16;
    private double attackCooldown = 0;
    private final double attackSpeed = 60;

    private enum AIState { IDLE, CHASING, ATTACKING }
    private AIState currentState = AIState.IDLE;
    private final Player player;

    public Enemy(double x, double y, int width, int height, Sprite sprite, Player player) {
        super(x, y, width, height);
        this.sprite = sprite;
        this.player = player;
        
        // Substitui o 'movement' genérico pelo nosso componente de IA
        this.movement = new AIMovementComponent(this, 0.8);
        
        ((AIMovementComponent) this.movement).avoidOtherActors = false;
        
        this.maxLife = 20;
        this.life = this.maxLife;
    }

    @Override
    public void tick() {
        super.tick();
        if (isDead) return;
        
        if (attackCooldown > 0) attackCooldown--;
        double distanceToPlayer = calculateDistance(this, player);
        
        // Faz o "cast" para acessar os métodos específicos de IA
        AIMovementComponent aiMovement = (AIMovementComponent) this.movement;

        switch (currentState) {
            case IDLE:
                aiMovement.setTarget(null);
                if (distanceToPlayer < visionRadius) currentState = AIState.CHASING;
                break;
            case CHASING:
               // aiMovement.setTarget(player);
                if (distanceToPlayer > visionRadius) currentState = AIState.IDLE;
                else if (distanceToPlayer < attackRadius) currentState = AIState.ATTACKING;
                break;
            case ATTACKING:
                aiMovement.setTarget(null);
                if (distanceToPlayer > attackRadius) currentState = AIState.CHASING;
                else if (attackCooldown <= 0) {
                    player.takeDamage(10);
                    attackCooldown = attackSpeed;
                }
                break;
        }
        
        // Atualiza o componente de movimento
        this.movement.tick();
    }

    private double calculateDistance(GameObject obj1, GameObject obj2) {
        int dx = obj1.getX() - obj2.getX();
        int dy = obj1.getY() - obj2.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
}