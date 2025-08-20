package com.JDStudio.Engine.Object.PreBuildObjcts;

import org.json.JSONObject;

import com.JDStudio.Engine.Components.InteractionComponent;
import com.JDStudio.Engine.Components.InteractionZone;
import com.JDStudio.Engine.Core.ISavable;
import com.JDStudio.Engine.Graphics.Sprite.Animations.Animator;
import com.JDStudio.Engine.Object.GameObject;
import com.JDStudio.Engine.Utils.PropertiesReader;

/**
 * Uma classe base "engine-side" para qualquer tipo de portal/teletransporte.
 * Contém toda a lógica de teletransporte, animação e condições de ativação.
 * A subclasse do jogo é responsável por fornecer as animações específicas e definir o destino.
 */
public abstract class EnginePortal extends GameObject implements ISavable {

    protected boolean isActive = true;
    protected boolean isUsed = false;
    protected Animator animator;
    protected String portalId;
    protected String destinationLevel;
    protected int destinationX;
    protected int destinationY;
    protected String destinationPortalId;
    protected boolean requiresKey = false;
    protected String requiredKeyId;
    protected boolean requiresQuest = false;
    protected String requiredQuestId;
    protected boolean autoTeleport = false;
    protected int cooldownTime = 0; // Em ticks
    protected int currentCooldown = 0;
    protected boolean oneTimeUse = false;

    public EnginePortal(JSONObject properties) {
        super(properties);
    }

    @Override
    public void initialize(JSONObject properties) {
        super.initialize(properties);
        PropertiesReader reader = new PropertiesReader(properties);
        
        this.isActive = reader.getBoolean("isActive", true);
        this.portalId = reader.getString("portalId", "");
        this.destinationLevel = reader.getString("destinationLevel", "");
        this.destinationX = reader.getInt("destinationX", 0);
        this.destinationY = reader.getInt("destinationY", 0);
        this.destinationPortalId = reader.getString("destinationPortalId", "");
        this.requiresKey = reader.getBoolean("requiresKey", false);
        this.requiredKeyId = reader.getString("requiredKeyId", "");
        this.requiresQuest = reader.getBoolean("requiresQuest", false);
        this.requiredQuestId = reader.getString("requiredQuestId", "");
        this.autoTeleport = reader.getBoolean("autoTeleport", false);
        this.cooldownTime = reader.getInt("cooldownTime", 60); // 1 segundo a 60 FPS
        this.oneTimeUse = reader.getBoolean("oneTimeUse", false);
        this.isUsed = reader.getBoolean("isUsed", false);

        this.animator = new Animator();
        this.addComponent(animator);
        
        // Chama o método abstrato que a classe do JOGO irá implementar
        setupAnimations(this.animator);

        // Se não é auto teleport, adiciona zona de interação
        if (!autoTeleport) {
            InteractionComponent interaction = new InteractionComponent();
            interaction.addZone(new InteractionZone(this, InteractionZone.TYPE_DIALOGUE, 24.0));
            this.addComponent(interaction);
        }

        updateStateVisuals();
        setCollisionType(autoTeleport ? CollisionType.TRIGGER : CollisionType.SOLID);
    }
    
    /**
     * Método principal de interação com o portal (para teleporte manual)
     */
    public void interact() {
        if (!isActive || currentCooldown > 0) return;
        if (oneTimeUse && isUsed) return;
        if (!animator.getCurrentAnimationKey().startsWith("idle")) return;

        // Verifica condições de uso
        if (!canUsePortal()) {
            return;
        }

        teleportPlayer();
    }

    /**
     * Método para teleporte automático (chamado por colisão)
     */
    public void onCollision(GameObject other) {
        if (!autoTeleport || !isActive || currentCooldown > 0) return;
        if (oneTimeUse && isUsed) return;
        
        // Verifica se é o jogador colidindo
        if (isPlayer(other)) {
            // Verifica condições de uso
            if (!canUsePortal()) {
                return;
            }
            
            teleportPlayer();
        }
    }

    /**
     * Verifica se o portal pode ser usado
     */
    protected boolean canUsePortal() {
        // Verifica se precisa de chave
        if (requiresKey && !hasRequiredKey()) {
            onKeyRequired();
            return false;
        }
        
        // Verifica se precisa de quest
        if (requiresQuest && !hasRequiredQuest()) {
            onQuestRequired();
            return false;
        }
        
        return true;
    }

    /**
     * Executa o teletransporte
     */
    protected void teleportPlayer() {
        if (!isActive) return;
        
        animator.play("activating");
        currentCooldown = cooldownTime;
        
        if (oneTimeUse) {
            isUsed = true;
            isActive = false;
        }
        
        // Chama o método abstrato para executar o teletransporte específico do jogo
        performTeleport(destinationLevel, destinationX, destinationY, destinationPortalId);
        
        // Notifica que o portal foi usado
        onPortalUsed();
    }

    /**
     * Ativa o portal
     */
    public void activate() {
        if (oneTimeUse && isUsed) return;
        
        isActive = true;
        updateStateVisuals();
        onPortalActivated();
    }

    /**
     * Desativa o portal
     */
    public void deactivate() {
        isActive = false;
        updateStateVisuals();
        onPortalDeactivated();
    }

    @Override
    public void tick() {
        super.tick();
        
        // Gerencia o cooldown
        if (currentCooldown > 0) {
            currentCooldown--;
        }
        
        // Gerencia as animações
        if (animator.getCurrentAnimation() != null && animator.getCurrentAnimation().hasFinished()) {
            String currentKey = animator.getCurrentAnimationKey();
            
            if ("activating".equals(currentKey)) {
                updateStateVisuals();
            }
        }
    }
    
    /**
     * Atualiza os visuais baseado no estado atual
     */
    private void updateStateVisuals() {
        if (!isActive || (oneTimeUse && isUsed)) {
            animator.play("idleInactive");
        } else if (currentCooldown > 0) {
            animator.play("idleCooldown");
        } else {
            animator.play("idleActive");
        }
    }

    @Override
    public JSONObject saveState() {
        JSONObject state = new JSONObject();
        state.put("name", this.name);
        state.put("isActive", this.isActive);
        state.put("isUsed", this.isUsed);
        state.put("currentCooldown", this.currentCooldown);
        return state;
    }

    @Override
    public void loadState(JSONObject state) {
        this.isActive = state.getBoolean("isActive");
        this.isUsed = state.getBoolean("isUsed");
        this.currentCooldown = state.getInt("currentCooldown");
        updateStateVisuals();
    }
    
    // Getters
    public boolean isActive() { return isActive; }
    public boolean isUsed() { return isUsed; }
    public String getPortalId() { return portalId; }
    public String getDestinationLevel() { return destinationLevel; }
    public int getDestinationX() { return destinationX; }
    public int getDestinationY() { return destinationY; }
    public String getDestinationPortalId() { return destinationPortalId; }
    public boolean requiresKey() { return requiresKey; }
    public String getRequiredKeyId() { return requiredKeyId; }
    public boolean requiresQuest() { return requiresQuest; }
    public String getRequiredQuestId() { return requiredQuestId; }
    public boolean isAutoTeleport() { return autoTeleport; }
    public int getCooldownTime() { return cooldownTime; }
    public int getCurrentCooldown() { return currentCooldown; }
    public boolean isOnCooldown() { return currentCooldown > 0; }
    public boolean isOneTimeUse() { return oneTimeUse; }
    
    /**
     * MÉTODOS ABSTRATOS: A classe do jogo DEVE implementar estes métodos
     */
    
    /**
     * Configura as animações específicas do portal
     * @param animator O componente Animator a ser configurado
     */
    protected abstract void setupAnimations(Animator animator);
    
    /**
     * Verifica se o jogador possui a chave necessária
     * @return true se possui a chave ou se não é necessária
     */
    protected abstract boolean hasRequiredKey();
    
    /**
     * Verifica se o jogador completou a quest necessária
     * @return true se completou a quest ou se não é necessária
     */
    protected abstract boolean hasRequiredQuest();
    
    /**
     * Verifica se o GameObject é o jogador
     * @param gameObject O GameObject a ser verificado
     * @return true se for o jogador
     */
    protected abstract boolean isPlayer(GameObject gameObject);
    
    /**
     * Executa o teletransporte específico do jogo
     * @param level O nível de destino
     * @param x A coordenada X de destino
     * @param y A coordenada Y de destino
     * @param portalId O ID do portal de destino (opcional)
     */
    protected abstract void performTeleport(String level, int x, int y, String portalId);
    
    /**
     * MÉTODOS OPCIONAIS: A classe do jogo pode sobrescrever para comportamento customizado
     */
    
    /**
     * Chamado quando o portal é usado
     */
    protected void onPortalUsed() {
        // Implementação padrão vazia - pode ser sobrescrita
    }
    
    /**
     * Chamado quando o portal é ativado
     */
    protected void onPortalActivated() {
        // Implementação padrão vazia - pode ser sobrescrita
    }
    
    /**
     * Chamado quando o portal é desativado
     */
    protected void onPortalDeactivated() {
        // Implementação padrão vazia - pode ser sobrescrita
    }
    
    /**
     * Chamado quando o jogador tenta usar um portal que requer chave
     */
    protected void onKeyRequired() {
        // Implementação padrão vazia - pode ser sobrescrita
    }
    
    /**
     * Chamado quando o jogador tenta usar um portal que requer quest
     */
    protected void onQuestRequired() {
        // Implementação padrão vazia - pode ser sobrescrita
    }
}
