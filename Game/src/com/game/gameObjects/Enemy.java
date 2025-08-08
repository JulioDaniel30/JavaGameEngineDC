// game
package com.game.gameObjects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;

import org.json.JSONObject;

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Components.HealthComponent;
import com.JDStudio.Engine.Components.PathComponent;
import com.JDStudio.Engine.Components.PathComponent.PatrolMode;
import com.JDStudio.Engine.Components.Moviments.AIMovementComponent;
import com.JDStudio.Engine.Core.ISavable;
import com.JDStudio.Engine.Graphics.Effects.ParticleManager;
import com.JDStudio.Engine.Graphics.Sprite.Animations.Animator;
import com.JDStudio.Engine.Object.Character;
import com.JDStudio.Engine.Object.GameObject;
import com.JDStudio.Engine.Sound.Sound;
import com.JDStudio.Engine.Sound.Sound.SoundChannel;
import com.JDStudio.Engine.Utils.PropertiesReader;
import com.game.States.PlayingState;

public class Enemy extends Character implements ISavable{

    
	// Referências para acesso rápido (estilo híbrido)
    private Animator animator;
    private AIMovementComponent aiMovement;

    // Estado do Inimigo
    private Player player;
    private int visionRadius;
    private int attackRadius;
    private double attackCooldown;
    private double attackSpeed;
    private double patrolArrivalThreshold;
    private PathComponent pathComponent;
    private enum AIState { IDLE, PATROLLING, CHASING, ATTACKING }
    private AIState currentState = AIState.IDLE;

    public Enemy(Player player, JSONObject properties) {
        super(properties);
        this.player = player;
        System.out.println(">>> [LOG Enemy] Construtor do Inimigo chamado. ID do Objeto: " + this.hashCode());
    }

    @Override
    public void initialize(JSONObject properties) {
        super.initialize(properties);
        System.out.println(">>> [LOG Enemy] Initialize do Inimigo '" + this.name + "' (ID: " + this.hashCode() + ") começando...");
        
        
        PropertiesReader reader = new PropertiesReader(properties);
        double speed = reader.getDouble("speed", 0.8);
        
        // 1. CRIA as instâncias dos componentes
        this.animator = new Animator();
        this.aiMovement = new AIMovementComponent(speed);
        System.out.println(">>> [LOG Enemy] Componentes criados. PRESTES A ADICIONAR...");
        
        // 2. ADICIONA os componentes ao sistema (O PASSO MAIS IMPORTANTE)
        // É esta parte que define o "owner" e corrige o erro.
        System.out.println("enemy");
        this.addComponent(this.animator);
        this.addComponent(this.aiMovement);
        System.out.println(">>> [LOG Enemy] Componentes ADICIONADOS.");
        // 3. CONFIGURA os componentes que acabaram de ser adicionados
        this.aiMovement.useAStarPathfinding = reader.getBoolean("useAStar", false);
        this.aiMovement.setTarget(this.player);
        setupAnimations(this.animator);
        setCollisionMask(2, 2, 12, 13);
        
        // Lê o resto das propriedades
        this.visionRadius = reader.getInt("visionRadius", 80);
        this.attackRadius = reader.getInt("attackRadius", 16);
        this.attackSpeed = reader.getDouble("attackSpeed", 60);
        this.life = reader.getInt("life", 20);
        this.maxLife = reader.getInt("maxLife", 20);
        this.patrolArrivalThreshold = reader.getDouble("patrolArrivalThreshold", 8.0);
        this.addComponent(new HealthComponent((int)this.maxLife));
    }

    private void setupAnimations(Animator animator) {
        animator.addAnimation("idle", new com.JDStudio.Engine.Graphics.Sprite.Animations.Animation(10, PlayingState.assets.getSprite("enemy")));
        animator.play("idle");
    }

    public void setPath(List<Point> path, PatrolMode mode) {
        if (path != null && !path.isEmpty()) {
            this.pathComponent = new PathComponent(this, path, mode, this.patrolArrivalThreshold);
            this.currentState = AIState.PATROLLING;
        }
    }
    
    @Override
    public void takeDamage(double amount) {
    	
    	super.takeDamage(amount);
    	getComponent(HealthComponent.class).takeDamage((int)amount);
    }
    
    @Override
    protected void die() {
    	super.die();
    	ParticleManager.getInstance().createExplosion(
                x + width / 2.0,
                y + height / 2.0,
                100,                               // 100 partículas
                Color.RED,                      // Cor inicial laranja
                new Color(150, 0, 0, 0),         // Cor final vermelho escuro e transparente
                30, 60,                            // Vida entre 30 e 60 frames
                0.5, 2.5,                          // Velocidade entre 0.5 e 2.5 pixels/frame
                8, 0                               // Tamanho começa em 8 e termina em 0
            );
    	Sound.play("/hurt.wav",SoundChannel.SFX, this.getX(), this.getY() );
    }
    
   @Override
    public void tick() {
        if (isDestroyed || isDead) return;
        
        super.tick(); // ATUALIZA TODOS OS COMPONENTES

	    if (attackCooldown > 0) attackCooldown--;
	
	    double distanceToPlayer = calculateDistance(this, player);
	
        // A máquina de estados agora controla toda a lógica de transição.
	    switch (currentState) {
	        case IDLE:
	            aiMovement.setTarget(null);
	            // Se detetar o jogador...
	            if (distanceToPlayer < visionRadius) {
	                currentState = AIState.CHASING; // ...e muda de estado.
	            }
	            break;

	        case PATROLLING:
	            if (pathComponent != null) {
	                pathComponent.update();
	                Point targetPoint = pathComponent.getTargetPosition();
	                if (targetPoint != null) aiMovement.setTarget(targetPoint.x, targetPoint.y);
	            }
	            // Se detetar o jogador...
	            if (distanceToPlayer < visionRadius) {
                   this.say("Onde pensa que vai?", 2.0f); // ...fala...
	                currentState = AIState.CHASING; // ...e muda de estado.
	            }
	            break;

	        case CHASING:
	            aiMovement.setTarget(player);
	            if (distanceToPlayer > visionRadius) {
	                // Se perder o jogador de vista, volta a patrulhar ou fica parado.
	                currentState = (pathComponent != null) ? AIState.PATROLLING : AIState.IDLE;
	            } else if (distanceToPlayer < attackRadius) {
	                currentState = AIState.ATTACKING;
	            }
	            break;

	        case ATTACKING:
	            aiMovement.setTarget(null); // Para de se mover para atacar
	            if (attackCooldown <= 0) {
	                player.takeDamage(player.maxLife/6);
	                attackCooldown = attackSpeed;
	            }
	            if (distanceToPlayer > attackRadius) { 
	            	currentState = AIState.CHASING;
	            }
	            break;
	    }
        
	}
    private double calculateDistance(GameObject obj1, GameObject obj2) {
        
        double obj1CenterX = obj1.getX() + obj1.getWidth() / 2.0;
        double obj1CenterY = obj1.getY() + obj1.getHeight() / 2.0;

        // Calcula o ponto central do obj2
        double obj2CenterX = obj2.getX() + obj2.getWidth() / 2.0;
        double obj2CenterY = obj2.getY() + obj2.getHeight() / 2.0;

        // Calcula a distância entre os centros
        double dx = obj1CenterX - obj2CenterX;
        double dy = obj1CenterY - obj2CenterY;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    @Override
    public void render(Graphics g) {
    	if(isDestroyed) return;
    	super.render(g);
    }
    
    @Override
    public void renderDebug(Graphics g) {
    	super.renderDebug(g);
        
    	
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
    	
        /*g.setColor(Color.GREEN);
        g.drawRect(
            (int)x - Engine.camera.getX(),
            (int)y - Engine.camera.getY(),
            width,
            height
        );*/
        g.setColor(Color.RED);
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

    @Override
    public JSONObject saveState() {
        JSONObject state = new JSONObject();
        state.put("name", this.name);
        state.put("x", this.x);
        state.put("y", this.y);
        state.put("life", this.life);
        state.put("isDestroyed", this.isDestroyed); // Importante para inimigos mortos
        return state;
    }

    @Override
    public void loadState(JSONObject state) {
        this.x = state.getDouble("x");
        this.y = state.getDouble("y");
        this.life = state.getDouble("life");
        this.isDestroyed = state.getBoolean("isDestroyed");

        if (this.isDestroyed) {
            this.isDead = true;
            setCollisionType(CollisionType.NO_COLLISION); // Desativa a colisão
        } else {
            this.isDead = false;
            setCollisionType(CollisionType.TRIGGER); // Reativa a colisão para o estado "vivo"
        }
    }
    
}