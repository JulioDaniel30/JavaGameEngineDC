package com.jdstudio.engine.Object.PreBuildObjcts;

import org.json.JSONObject;

import com.jdstudio.engine.Components.InteractionComponent;
import com.jdstudio.engine.Components.InteractionZone;
import com.jdstudio.engine.Core.ISavable;
import com.jdstudio.engine.Graphics.Sprite.Animations.Animator;
import com.jdstudio.engine.Object.GameObject;
import com.jdstudio.engine.Utils.PropertiesReader;

/**
 * Uma classe base "engine-side" para qualquer tipo de baú/cofre.
 * Contém toda a lógica de estado (aberto/fechado), animação, interação e sistema de loot.
 * A subclasse do jogo é responsável por fornecer as animações específicas e definir o loot.
 */
public abstract class EngineChest extends GameObject implements ISavable {

    protected boolean isOpen = false;
    protected boolean hasBeenLooted = false;
    protected Animator animator;
    protected String lootTable;
    protected boolean requiresKey = false;
    protected String requiredKeyId;

    public EngineChest(JSONObject properties) {
        super(properties);
    }

    @Override
    public void initialize(JSONObject properties) {
        super.initialize(properties);
        PropertiesReader reader = new PropertiesReader(properties);
        
        this.isOpen = reader.getBoolean("startsOpen", false);
        this.hasBeenLooted = reader.getBoolean("hasBeenLooted", false);
        this.lootTable = reader.getString("lootTable", "");
        this.requiresKey = reader.getBoolean("requiresKey", false);
        this.requiredKeyId = reader.getString("requiredKeyId", "");

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
     * Método principal de interação com o baú
     */
    public void interact() {
        if (!animator.getCurrentAnimationKey().startsWith("idle")) return;

        // Se já está aberto e foi saqueado, não faz nada
        if (isOpen && hasBeenLooted) {
            onAlreadyLooted();
            return;
        }

        // Se está fechado, tenta abrir
        if (!isOpen) {
            if (requiresKey && !hasRequiredKey()) {
                onKeyRequired();
                return;
            }
            
            // Abre o baú
            animator.play("opening");
            isOpen = true;
            onChestOpened();
        }
        
        // Se está aberto mas não foi saqueado, permite saquear
        if (isOpen && !hasBeenLooted) {
            lootChest();
        }
    }

    /**
     * Executa o saque do baú
     */
    protected void lootChest() {
        if (hasBeenLooted) return;
        
        hasBeenLooted = true;
        
        // Chama o método abstrato para dar o loot específico do jogo
        giveLoot(lootTable);
        
        // Notifica que o baú foi saqueado
        onChestLooted();
    }

    @Override
    public void tick() {
        super.tick();
        
        if (animator.getCurrentAnimation() != null && animator.getCurrentAnimation().hasFinished()) {
            if ("opening".equals(animator.getCurrentAnimationKey())) {
                if (hasBeenLooted) {
                    animator.play("idleOpenEmpty");
                } else {
                    animator.play("idleOpenFull");
                }
            }
        }
    }
    
    /**
     * Atualiza os visuais baseado no estado atual
     */
    private void updateStateVisuals() {
        if (isOpen) {
            if (hasBeenLooted) {
                animator.play("idleOpenEmpty");
            } else {
                animator.play("idleOpenFull");
            }
        } else {
            animator.play("idleClosed");
        }
    }

    @Override
    public JSONObject saveState() {
        JSONObject state = new JSONObject();
        state.put("name", this.name);
        state.put("isOpen", this.isOpen);
        state.put("hasBeenLooted", this.hasBeenLooted);
        return state;
    }

    @Override
    public void loadState(JSONObject state) {
        this.isOpen = state.getBoolean("isOpen");
        this.hasBeenLooted = state.getBoolean("hasBeenLooted");
        updateStateVisuals();
    }
    
    // Getters
    public boolean isOpen() { return isOpen; }
    public boolean hasBeenLooted() { return hasBeenLooted; }
    public String getLootTable() { return lootTable; }
    public boolean requiresKey() { return requiresKey; }
    public String getRequiredKeyId() { return requiredKeyId; }
    
    /**
     * MÉTODOS ABSTRATOS: A classe do jogo DEVE implementar estes métodos
     */
    
    /**
     * Configura as animações específicas do baú
     * @param animator O componente Animator a ser configurado
     */
    protected abstract void setupAnimations(Animator animator);
    
    /**
     * Verifica se o jogador possui a chave necessária
     * @return true se possui a chave ou se não é necessária
     */
    protected abstract boolean hasRequiredKey();
    
    /**
     * Dá o loot específico do jogo ao jogador
     * @param lootTable A tabela de loot a ser usada
     */
    protected abstract void giveLoot(String lootTable);
    
    /**
     * MÉTODOS OPCIONAIS: A classe do jogo pode sobrescrever para comportamento customizado
     */
    
    /**
     * Chamado quando o baú é aberto pela primeira vez
     */
    protected void onChestOpened() {
        // Implementação padrão vazia - pode ser sobrescrita
    }
    
    /**
     * Chamado quando o baú é saqueado
     */
    protected void onChestLooted() {
        // Implementação padrão vazia - pode ser sobrescrita
    }
    
    /**
     * Chamado quando o jogador tenta abrir um baú que requer chave
     */
    protected void onKeyRequired() {
        // Implementação padrão vazia - pode ser sobrescrita
    }
    
    /**
     * Chamado quando o jogador interage com um baú já saqueado
     */
    protected void onAlreadyLooted() {
        // Implementação padrão vazia - pode ser sobrescrita
    }
}
