// game
package com.game.gameObjects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Components.HealthComponent;
import com.jdstudio.engine.Components.InteractionComponent;
import com.jdstudio.engine.Components.InteractionZone;
import com.jdstudio.engine.Components.PathComponent;
import com.jdstudio.engine.Components.PathComponent.PatrolMode;
import com.jdstudio.engine.Components.Moviments.AIMovementComponent;
import com.jdstudio.engine.Core.ISavable;
import com.jdstudio.engine.Events.EngineEvent;
import com.jdstudio.engine.Events.EventListener;
import com.jdstudio.engine.Events.EventManager;
import com.jdstudio.engine.Events.InteractionEventData;
import com.jdstudio.engine.Graphics.Effects.ParticleManager;
import com.jdstudio.engine.Graphics.Sprite.Animations.Animator;
import com.jdstudio.engine.Object.Character;
import com.jdstudio.engine.Object.GameObject;
import com.jdstudio.engine.Sound.Sound;
import com.jdstudio.engine.Sound.Sound.SoundChannel;
import com.jdstudio.engine.Utils.PropertiesReader;
import com.game.States.PlayingState;

public class Enemy extends Character implements ISavable{

    
	// Referências para acesso rápido (estilo híbrido)
    private Animator animator;
    private AIMovementComponent aiMovement;
    
    private EventListener onEnterZoneListener;
    private EventListener onExitZoneListener;

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
    
    // 1. CRIA e ADICIONA os componentes
    this.animator = new Animator();
    this.aiMovement = new AIMovementComponent(speed);
    this.addComponent(this.animator);
    this.addComponent(this.aiMovement);
    this.addComponent(new HealthComponent((int)reader.getInt("maxLife", 20)));
    
    // 2. CONFIGURA os componentes
    this.aiMovement.useAStarPathfinding = reader.getBoolean("useAStar", false);
    // Não defina o alvo aqui, deixe os eventos controlarem isso.
    // this.aiMovement.setTarget(this.player); 
    setupAnimations(this.animator);
    setCollisionMask(2, 2, 12, 13);
    
    // 3. LÊ as propriedades para a IA e INTERAÇÃO
    this.visionRadius = reader.getInt("visionRadius", 0); // Default de 80
    this.attackRadius = reader.getInt("attackRadius", 14); // Default de 16
    this.attackSpeed = reader.getDouble("attackSpeed", 60);
    this.patrolArrivalThreshold = reader.getDouble("patrolArrivalThreshold", 8.0);
    
    // 4. CRIA e CONFIGURA o InteractionComponent com os valores corretos
    InteractionComponent interaction = new InteractionComponent();
    
 // **A LÓGICA DE CONTROLO ESTÁ AQUI**
    // Só adiciona a zona de perseguição se o raio de visão for positivo.
    if (this.visionRadius > 0) {
        interaction.addZone(new InteractionZone(this, InteractionZone.TYPE_AGGRO, this.visionRadius));
    }
    interaction.addZone(new InteractionZone(this, InteractionZone.TYPE_ATTACK, this.attackRadius));
    
    this.addComponent(interaction);

    // 5. INSCREVE os listeners
    setupEventListeners();
}
    
     /**
     /**
     * Configura os listeners para que o inimigo reaja aos seus próprios eventos de zona.
     */
    private void setupEventListeners() {
        final Enemy self = this; 

        this.onEnterZoneListener = (data) -> {
            InteractionEventData event = (InteractionEventData) data;
            if (event.zoneOwner() != self) return;

            String zoneType = event.zone().type;
            switch (zoneType) {
                case "AGGRO":
                    self.say("Onde pensa que vai?", 2.0f);
                    self.currentState = AIState.CHASING;
                    self.aiMovement.setTarget(self.player);
                    break;
                case "ATTACK":
                    self.currentState = AIState.ATTACKING;
                    self.aiMovement.setTarget(null); 
                    break;
            }
        };

        this.onExitZoneListener = (data) -> {
            InteractionEventData event = (InteractionEventData) data;
            if (event.zoneOwner() != self) return;

            String zoneType = event.zone().type;
            switch (zoneType) {
                case "AGGRO":
                    // Se saiu da área de aggro, volta ao estado padrão (patrulha ou idle).
                    self.currentState = (self.hasPathComponent()) ? AIState.PATROLLING : AIState.IDLE;
                    self.aiMovement.setTarget(null);
                    break;
                case "ATTACK":
                    // --- A LÓGICA CORRIGIDA ESTÁ AQUI ---
                    // Verifica se este inimigo tem uma zona de perseguição.
                    if (self.visionRadius > 0) {
                        // Se SIM, ele volta a perseguir.
                        self.currentState = AIState.CHASING;
                        self.aiMovement.setTarget(self.player);
                    } else {
                        // Se NÃO, ele volta ao seu estado padrão.
                        self.currentState = (self.hasPathComponent()) ? AIState.PATROLLING : AIState.IDLE;
                        self.aiMovement.setTarget(null);
                    }
                    break;
            }
        };

        EventManager.getInstance().subscribe(EngineEvent.TARGET_ENTERED_ZONE, onEnterZoneListener);
        EventManager.getInstance().subscribe(EngineEvent.TARGET_EXITED_ZONE, onExitZoneListener);
    }


    private void setupAnimations(Animator animator) {
        animator.addAnimation("idle", new com.jdstudio.engine.Graphics.Sprite.Animations.Animation(10, PlayingState.assets.getSprite("enemy")));
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
    
    /**
     * Verifica se este inimigo tem um componente de patrulha configurado.
     * @return true se o inimigo tiver uma rota para patrulhar, false caso contrário.
     */
    public boolean hasPathComponent() {
        return this.pathComponent != null;
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
    	EventManager.getInstance().unsubscribe(EngineEvent.TARGET_ENTERED_ZONE, onEnterZoneListener);
        EventManager.getInstance().unsubscribe(EngineEvent.TARGET_EXITED_ZONE, onExitZoneListener);
    }
    
   @Override
public void tick() {
    // 1. Verificações iniciais
    if (isDestroyed || isDead) return;
    
    // 2. Atualiza todos os componentes (movimento, animação, etc.)
    super.tick(); 
    
    // 3. Pede ao InteractionComponent para verificar as zonas e disparar eventos.
    // É esta linha que, indiretamente, irá acionar a mudança de estado nos listeners.
    getComponent(InteractionComponent.class).checkInteractions(Collections.singletonList(player));

    // 4. O 'switch' agora apenas EXECUTA o comportamento do estado atual.
    // Ele já não tem 'if's para mudar de estado.
    switch (currentState) {
        case IDLE:
            // O comportamento de IDLE já foi definido pelo EventListener (alvo nulo).
            // Não precisa de fazer nada aqui.
            break;

        case PATROLLING:
            // Comportamento: Seguir o caminho de patrulha.
            if (pathComponent != null) {
                pathComponent.update();
                Point targetPoint = pathComponent.getTargetPosition();
                if (targetPoint != null) aiMovement.setTarget(targetPoint.x, targetPoint.y);
            }
            break;

        case CHASING:
            // O comportamento de CHASING já foi definido pelo EventListener (alvo = jogador).
            // Não precisa de fazer nada aqui.
            break;

        case ATTACKING:
            // Comportamento: Executar a lógica de ataque.
            if (attackCooldown <= 0) {
                player.takeDamage(10); // Dano de exemplo
                attackCooldown = attackSpeed;
            }
            break;
    }
    
    // 5. Atualiza o cooldown de ataque, independentemente do estado.
    if (attackCooldown > 0) {
        attackCooldown--;
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