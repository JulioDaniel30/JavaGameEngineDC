package com.JDStudio.Engine.Object.PreBuildObjcts;

import org.json.JSONObject;

import com.JDStudio.Engine.Components.InteractionComponent;
import com.JDStudio.Engine.Components.InteractionZone;
import com.JDStudio.Engine.Core.ISavable;
import com.JDStudio.Engine.Graphics.Sprite.Animations.Animator;
import com.JDStudio.Engine.Object.GameObject;
import com.JDStudio.Engine.Utils.PropertiesReader;

/**
 * Uma classe base "engine-side" para qualquer tipo de item coletável.
 * Contém toda a lógica de coleta, animação e integração com inventário.
 * A subclasse do jogo é responsável por fornecer as animações específicas e definir o item.
 */
public abstract class EnginePickup extends GameObject implements ISavable {

    protected boolean isCollected = false;
    protected Animator animator;
    protected String itemId;
    protected int quantity = 1;
    protected boolean autoPickup = false;
    protected boolean respawns = false;
    protected int respawnTime = 0; // Em ticks
    protected int currentRespawnTimer = 0;
    protected boolean requiresKey = false;
    protected String requiredKeyId;

    public EnginePickup(JSONObject properties) {
        super(properties);
    }

    @Override
    public void initialize(JSONObject properties) {
        super.initialize(properties);
        PropertiesReader reader = new PropertiesReader(properties);
        
        this.itemId = reader.getString("itemId", "");
        this.quantity = reader.getInt("quantity", 1);
        this.autoPickup = reader.getBoolean("autoPickup", false);
        this.respawns = reader.getBoolean("respawns", false);
        this.respawnTime = reader.getInt("respawnTime", 300); // 5 segundos a 60 FPS
        this.requiresKey = reader.getBoolean("requiresKey", false);
        this.requiredKeyId = reader.getString("requiredKeyId", "");
        this.isCollected = reader.getBoolean("isCollected", false);

        this.animator = new Animator();
        this.addComponent(animator);
        
        // Chama o método abstrato que a classe do JOGO irá implementar
        setupAnimations(this.animator);

        // Se não é auto pickup, adiciona zona de interação
        if (!autoPickup) {
            InteractionComponent interaction = new InteractionComponent();
            interaction.addZone(new InteractionZone(this, InteractionZone.TYPE_DIALOGUE, 24.0));
            this.addComponent(interaction);
        }

        updateStateVisuals();
        setCollisionType(autoPickup ? CollisionType.TRIGGER : CollisionType.SOLID);
    }
    
    /**
     * Método principal de interação com o item (para pickup manual)
     */
    public void interact() {
        if (isCollected) return;
        if (!animator.getCurrentAnimationKey().startsWith("idle")) return;

        // Verifica se precisa de chave
        if (requiresKey && !hasRequiredKey()) {
            onKeyRequired();
            return;
        }

        collectItem();
    }

    /**
     * Método para coleta automática (chamado por colisão)
     */
    public void onCollision(GameObject other) {
        if (!autoPickup || isCollected) return;
        
        // Verifica se é o jogador colidindo
        if (isPlayer(other)) {
            // Verifica se precisa de chave
            if (requiresKey && !hasRequiredKey()) {
                onKeyRequired();
                return;
            }
            
            collectItem();
        }
    }

    /**
     * Executa a coleta do item
     */
    protected void collectItem() {
        if (isCollected) return;
        
        isCollected = true;
        animator.play("collecting");
        
        // Chama o método abstrato para adicionar o item ao inventário
        giveItemToPlayer(itemId, quantity);
        
        // Notifica que o item foi coletado
        onItemCollected();
        
        // Se não respawna, marca para destruição
        if (!respawns) {
            setCollisionType(CollisionType.NO_COLLISION);
        } else {
            currentRespawnTimer = respawnTime;
        }
    }

    @Override
    public void tick() {
        super.tick();
        
        // Gerencia o respawn
        if (isCollected && respawns && currentRespawnTimer > 0) {
            currentRespawnTimer--;
            if (currentRespawnTimer <= 0) {
                respawnItem();
            }
        }
        
        // Gerencia as animações
        if (animator.getCurrentAnimation() != null && animator.getCurrentAnimation().hasFinished()) {
            String currentKey = animator.getCurrentAnimationKey();
            
            if ("collecting".equals(currentKey)) {
                if (respawns) {
                    animator.play("collected");
                } else {
                    // Item não respawna, pode ser destruído
                    this.isDestroyed = true;
                }
            } else if ("respawning".equals(currentKey)) {
                animator.play("idleAvailable");
            }
        }
    }
    
    /**
     * Respawna o item
     */
    protected void respawnItem() {
        isCollected = false;
        animator.play("respawning");
        setCollisionType(autoPickup ? CollisionType.TRIGGER : CollisionType.SOLID);
        onItemRespawned();
    }
    
    /**
     * Atualiza os visuais baseado no estado atual
     */
    private void updateStateVisuals() {
        if (isCollected) {
            if (respawns) {
                animator.play("collected");
            } else {
                // Item coletado e não respawna - pode ser invisível
                animator.play("collected");
            }
        } else {
            animator.play("idleAvailable");
        }
    }

    @Override
    public JSONObject saveState() {
        JSONObject state = new JSONObject();
        state.put("name", this.name);
        state.put("isCollected", this.isCollected);
        state.put("currentRespawnTimer", this.currentRespawnTimer);
        return state;
    }

    @Override
    public void loadState(JSONObject state) {
        this.isCollected = state.getBoolean("isCollected");
        this.currentRespawnTimer = state.getInt("currentRespawnTimer");
        updateStateVisuals();
        
        // Atualiza o tipo de colisão baseado no estado
        if (isCollected && !respawns) {
            setCollisionType(CollisionType.NO_COLLISION);
        } else {
            setCollisionType(autoPickup ? CollisionType.TRIGGER : CollisionType.SOLID);
        }
    }
    
    // Getters
    public boolean isCollected() { return isCollected; }
    public String getItemId() { return itemId; }
    public int getQuantity() { return quantity; }
    public boolean isAutoPickup() { return autoPickup; }
    public boolean respawns() { return respawns; }
    public int getRespawnTime() { return respawnTime; }
    public int getCurrentRespawnTimer() { return currentRespawnTimer; }
    public boolean requiresKey() { return requiresKey; }
    public String getRequiredKeyId() { return requiredKeyId; }
    
    /**
     * MÉTODOS ABSTRATOS: A classe do jogo DEVE implementar estes métodos
     */
    
    /**
     * Configura as animações específicas do item
     * @param animator O componente Animator a ser configurado
     */
    protected abstract void setupAnimations(Animator animator);
    
    /**
     * Verifica se o jogador possui a chave necessária
     * @return true se possui a chave ou se não é necessária
     */
    protected abstract boolean hasRequiredKey();
    
    /**
     * Verifica se o GameObject é o jogador
     * @param gameObject O GameObject a ser verificado
     * @return true se for o jogador
     */
    protected abstract boolean isPlayer(GameObject gameObject);
    
    /**
     * Adiciona o item ao inventário do jogador
     * @param itemId O ID do item a ser adicionado
     * @param quantity A quantidade do item
     */
    protected abstract void giveItemToPlayer(String itemId, int quantity);
    
    /**
     * MÉTODOS OPCIONAIS: A classe do jogo pode sobrescrever para comportamento customizado
     */
    
    /**
     * Chamado quando o item é coletado
     */
    protected void onItemCollected() {
        // Implementação padrão vazia - pode ser sobrescrita
    }
    
    /**
     * Chamado quando o item respawna
     */
    protected void onItemRespawned() {
        // Implementação padrão vazia - pode ser sobrescrita
    }
    
    /**
     * Chamado quando o jogador tenta coletar um item que requer chave
     */
    protected void onKeyRequired() {
        // Implementação padrão vazia - pode ser sobrescrita
    }
}
