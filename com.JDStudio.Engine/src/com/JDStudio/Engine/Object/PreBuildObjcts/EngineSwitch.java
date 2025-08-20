package com.JDStudio.Engine.Object.PreBuildObjcts;

import org.json.JSONObject;

import com.JDStudio.Engine.Components.InteractionComponent;
import com.JDStudio.Engine.Components.InteractionZone;
import com.JDStudio.Engine.Core.ISavable;
import com.JDStudio.Engine.Graphics.Sprite.Animations.Animator;
import com.JDStudio.Engine.Object.GameObject;
import com.JDStudio.Engine.Utils.PropertiesReader;

/**
 * Uma classe base "engine-side" para qualquer tipo de interruptor/alavanca.
 * Contém toda a lógica de estado (ligado/desligado), animação e sistema de eventos.
 * A subclasse do jogo é responsável por fornecer as animações específicas e definir as ações.
 */
public abstract class EngineSwitch extends GameObject implements ISavable {

    protected boolean isOn = false;
    protected Animator animator;
    protected String switchId;
    protected boolean isToggleable = true;
    protected boolean requiresKey = false;
    protected String requiredKeyId;
    protected int cooldownTime = 0; // Em ticks
    protected int currentCooldown = 0;

    public EngineSwitch(JSONObject properties) {
        super(properties);
    }

    @Override
    public void initialize(JSONObject properties) {
        super.initialize(properties);
        PropertiesReader reader = new PropertiesReader(properties);
        
        this.isOn = reader.getBoolean("startsOn", false);
        this.switchId = reader.getString("switchId", "");
        this.isToggleable = reader.getBoolean("isToggleable", true);
        this.requiresKey = reader.getBoolean("requiresKey", false);
        this.requiredKeyId = reader.getString("requiredKeyId", "");
        this.cooldownTime = reader.getInt("cooldownTime", 0);

        this.animator = new Animator();
        this.addComponent(animator);
        
        // Chama o método abstrato que a classe do JOGO irá implementar
        setupAnimations(this.animator);

        // Adiciona a zona de interação manual
        InteractionComponent interaction = new InteractionComponent();
        interaction.addZone(new InteractionZone(this, InteractionZone.TYPE_DIALOGUE, 24.0));
        this.addComponent(interaction);

        updateStateVisuals();
        setCollisionType(CollisionType.SOLID);
    }
    
    /**
     * Método principal de interação com o interruptor
     */
    public void interact() {
        if (!animator.getCurrentAnimationKey().startsWith("idle")) return;
        if (currentCooldown > 0) return;

        // Verifica se precisa de chave
        if (requiresKey && !hasRequiredKey()) {
            onKeyRequired();
            return;
        }

        // Se é toggleável, alterna o estado
        if (isToggleable) {
            if (isOn) {
                turnOff();
            } else {
                turnOn();
            }
        } else {
            // Se não é toggleável, apenas ativa temporariamente
            if (!isOn) {
                turnOn();
            }
        }
    }

    /**
     * Liga o interruptor
     */
    public void turnOn() {
        if (isOn) return;
        
        isOn = true;
        animator.play("turningOn");
        currentCooldown = cooldownTime;
        
        // Chama o método abstrato para executar a ação específica do jogo
        onSwitchActivated();
        
        // Notifica que o interruptor foi ligado
        onSwitchTurnedOn();
    }

    /**
     * Desliga o interruptor
     */
    public void turnOff() {
        if (!isOn) return;
        
        isOn = false;
        animator.play("turningOff");
        currentCooldown = cooldownTime;
        
        // Chama o método abstrato para executar a ação específica do jogo
        onSwitchDeactivated();
        
        // Notifica que o interruptor foi desligado
        onSwitchTurnedOff();
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
            
            if ("turningOn".equals(currentKey)) {
                animator.play("idleOn");
            } else if ("turningOff".equals(currentKey)) {
                animator.play("idleOff");
            }
        }
    }
    
    /**
     * Atualiza os visuais baseado no estado atual
     */
    private void updateStateVisuals() {
        if (isOn) {
            animator.play("idleOn");
        } else {
            animator.play("idleOff");
        }
    }

    @Override
    public JSONObject saveState() {
        JSONObject state = new JSONObject();
        state.put("name", this.name);
        state.put("isOn", this.isOn);
        state.put("currentCooldown", this.currentCooldown);
        return state;
    }

    @Override
    public void loadState(JSONObject state) {
        this.isOn = state.getBoolean("isOn");
        this.currentCooldown = state.getInt("currentCooldown");
        updateStateVisuals();
    }
    
    // Getters
    public boolean isOn() { return isOn; }
    public String getSwitchId() { return switchId; }
    public boolean isToggleable() { return isToggleable; }
    public boolean requiresKey() { return requiresKey; }
    public String getRequiredKeyId() { return requiredKeyId; }
    public int getCooldownTime() { return cooldownTime; }
    public int getCurrentCooldown() { return currentCooldown; }
    public boolean isOnCooldown() { return currentCooldown > 0; }
    
    /**
     * MÉTODOS ABSTRATOS: A classe do jogo DEVE implementar estes métodos
     */
    
    /**
     * Configura as animações específicas do interruptor
     * @param animator O componente Animator a ser configurado
     */
    protected abstract void setupAnimations(Animator animator);
    
    /**
     * Verifica se o jogador possui a chave necessária
     * @return true se possui a chave ou se não é necessária
     */
    protected abstract boolean hasRequiredKey();
    
    /**
     * Executado quando o interruptor é ativado (ligado)
     */
    protected abstract void onSwitchActivated();
    
    /**
     * Executado quando o interruptor é desativado (desligado)
     */
    protected abstract void onSwitchDeactivated();
    
    /**
     * MÉTODOS OPCIONAIS: A classe do jogo pode sobrescrever para comportamento customizado
     */
    
    /**
     * Chamado quando o interruptor é ligado
     */
    protected void onSwitchTurnedOn() {
        // Implementação padrão vazia - pode ser sobrescrita
    }
    
    /**
     * Chamado quando o interruptor é desligado
     */
    protected void onSwitchTurnedOff() {
        // Implementação padrão vazia - pode ser sobrescrita
    }
    
    /**
     * Chamado quando o jogador tenta usar um interruptor que requer chave
     */
    protected void onKeyRequired() {
        // Implementação padrão vazia - pode ser sobrescrita
    }
}
