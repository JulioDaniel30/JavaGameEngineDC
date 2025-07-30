// game
package com.game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;

import org.json.JSONObject;

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Components.PathComponent;
import com.JDStudio.Engine.Components.PathComponent.PatrolMode;
import com.JDStudio.Engine.Components.Moviments.AIMovementComponent;
import com.JDStudio.Engine.Object.Character;
import com.JDStudio.Engine.Object.GameObject;
import com.JDStudio.Engine.Utils.PropertiesReader;

public class Enemy extends Character {

    
    private final Player player;
    private final int visionRadius;
    private final int attackRadius;
    private double attackCooldown;
    private final double attackSpeed;

    private PathComponent pathComponent;
    private enum AIState { IDLE, PATROLLING, CHASING, ATTACKING }
    private AIState currentState = AIState.IDLE;
    public double speed;

    public Enemy(Player player, JSONObject properties) {
        super(properties); // Passa as propriedades para o construtor do Character
        this.player = player;

        // O initialize() chamado pelo super() já configurou vida, posição, etc.
        // Agora só precisamos ler o que é específico do Inimigo.
        PropertiesReader reader = new PropertiesReader(properties);
        String idleSpriteName = reader.getString("sprite_idle", "enemy");
        this.sprite = PlayingState.assets.getSprite(idleSpriteName);
        this.speed = reader.getDouble("speed", 0.8);
        this.visionRadius = reader.getInt("visionRadius", 80);
        this.attackRadius = reader.getInt("attackRadius", 16);
        this.attackCooldown = reader.getDouble("attackCooldown", 0);
        this.attackSpeed = reader.getDouble("attackSpeed", 60);
        this.life = reader.getDouble("life", 20);
        this.maxLife = reader.getInt("maxLife", 20);
        
        System.out.println( reader.getInt("visionRadius", 0));
     // Se a propriedade "useAStar" for true no Tiled, o inimigo será inteligente.
        // Caso contrário, ele se moverá em linha reta.
        
        
        this.movement = new AIMovementComponent(this, speed);
        ((AIMovementComponent) this.movement).avoidOtherActors = false;
        AIMovementComponent aiMovement = (AIMovementComponent) this.movement;
        
        
        aiMovement.useAStarPathfinding = reader.getBoolean("useAStar", false);
        aiMovement.setTarget(player);
        
        System.out.println(aiMovement.useAStarPathfinding);
        if(aiMovement.useAStarPathfinding) {
        	System.out.println(aiMovement.getTarget());
        }
       
    }
    
    @Override
    public void initialize(JSONObject properties) {
    	super.initialize(properties);
    	
    	
    }

    public void setPath(List<Point> path, PatrolMode mode) {
        if (path != null && !path.isEmpty()) {
            this.pathComponent = new PathComponent(this, path, mode);
            this.currentState = AIState.PATROLLING;
        }
    }
    
    @Override
    public void onCollision(GameObject other) {
    	// TODO Auto-generated method stub
    	super.onCollision(other);
    }

  
	@Override
	public void tick() {
	    super.tick(); // Atualiza componentes base como o Animator
	    if (isDead) return;
	
	    if (attackCooldown > 0) {
	        attackCooldown--;
	    }
	
	    double distanceToPlayer = calculateDistance(this, player);
	    AIMovementComponent aiMovement = (AIMovementComponent) this.movement;
	
	    // --- MÁQUINA DE ESTADOS CORRIGIDA ---
	    switch (currentState) {
	        case IDLE:
	            aiMovement.setTarget(null);
	            // TRANSIÇÃO:
	            if (distanceToPlayer < visionRadius) {
	                currentState = AIState.CHASING;
	            }
	            break;
	
	        case PATROLLING:
	            // AÇÃO:
	            if (pathComponent != null) {
	                pathComponent.update();
	                Point targetPoint = pathComponent.getTargetPosition();
	                if (targetPoint != null) {
	                    aiMovement.setTarget(targetPoint.x, targetPoint.y);
	                }
	            }
	            // TRANSIÇÃO:
	            if (distanceToPlayer < visionRadius) {
	                currentState = AIState.CHASING;
	            }
	            break;
	
	        case CHASING:
	            // AÇÃO:
	            aiMovement.setTarget(player);
	            
	            // --- LÓGICA DE TRANSIÇÃO FALTANTE ADICIONADA AQUI ---
	            // Se o jogador fugir para fora do raio de visão...
	            if (distanceToPlayer > visionRadius) {
	                // ...o inimigo volta a patrulhar (ou fica parado se não tiver caminho).
	                currentState = (pathComponent != null) ? AIState.PATROLLING : AIState.IDLE;
	            } 
	            // Se o jogador chegar perto o suficiente, ataca.
	            else if (distanceToPlayer < attackRadius) {
	                currentState = AIState.ATTACKING;
	            }
	            break;
	
	        case ATTACKING:
	            // AÇÃO:
	            aiMovement.setTarget(null);
	            if (attackCooldown <= 0) {
	                player.takeDamage(10);
	                attackCooldown = attackSpeed;
	            }
	            // TRANSIÇÃO:
	            if (distanceToPlayer > attackRadius) {
	                currentState = AIState.CHASING;
	            }
	            break;
	    }
	    
	    // O tick do movimento é sempre chamado no final para executar a ação definida pelo estado.
	    this.movement.tick();
	}
    private double calculateDistance(GameObject obj1, GameObject obj2) {
        int dx = obj1.getX() - obj2.getX();
        int dy = obj1.getY() - obj2.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    @Override
    public void renderDebug(Graphics g) {
    	if(!Engine.isDebug) return;
    	
    	
    	// --- DESENHA O RAIO DE VISÃO ---
        g.setColor(Color.ORANGE); // Uma cor para a detecção
        int centerX = getX() + getWidth() / 2;
        int centerY = getY() + getHeight() / 2;
        
        // Desenha um círculo representando o raio de visão
        g.drawOval(
            centerX - visionRadius - Engine.camera.getX(),
            centerY - visionRadius - Engine.camera.getY(),
            visionRadius * 2,
            visionRadius * 2
        );
    	
        g.setColor(Color.RED);
        g.drawRect(
            (int)x - Engine.camera.getX(),
            (int)y - Engine.camera.getY(),
            width,
            height
        );
        g.setColor(Color.GREEN);
        g.drawRect(
            (int)(x + maskX) - Engine.camera.getX(),
            (int)(y + maskY) - Engine.camera.getY(),
            maskWidth,
            maskHeight
        );

        if (pathComponent != null && pathComponent.getPath() != null) {
            g.setColor(Color.BLUE);
            List<Point> path = pathComponent.getPath();
            for (int i = 0; i < path.size(); i++) {
                Point p = path.get(i);
                g.fillRect(p.x - Engine.camera.getX() - 2, p.y - Engine.camera.getY() - 2, 4, 4);
                if (i > 0) {
                    Point prev = path.get(i - 1);
                    g.drawLine(prev.x - Engine.camera.getX(), prev.y - Engine.camera.getY(),
                               p.x - Engine.camera.getX(), p.y - Engine.camera.getY());
                }
            }
            // Desenha uma linha até o ponto alvo atual
            Point target = pathComponent.getTargetPosition();
            if (target != null) {
                g.setColor(Color.GREEN);
                g.drawLine(getX() - Engine.camera.getX() + getWidth() / 2,
                           getY() - Engine.camera.getY() + getHeight() / 2,
                           target.x - Engine.camera.getX(),
                           target.y - Engine.camera.getY());
            }
        }
    }
    
}