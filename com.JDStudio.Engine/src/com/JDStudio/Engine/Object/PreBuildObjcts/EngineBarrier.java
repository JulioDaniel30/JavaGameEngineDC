package com.JDStudio.Engine.Object.PreBuildObjcts;

import org.json.JSONObject;

import com.JDStudio.Engine.Components.InteractionComponent;
import com.JDStudio.Engine.Components.InteractionZone;
import com.JDStudio.Engine.Core.ISavable;
import com.JDStudio.Engine.Graphics.Sprite.Animations.Animator;
import com.JDStudio.Engine.Object.GameObject;
import com.JDStudio.Engine.Utils.PropertiesReader;

/**
 * Uma classe base "engine-side" para qualquer tipo de barreira destrutível.
 * Contém toda a lógica de vida, dano, destruição e animação.
 * A subclasse do jogo é responsável por fornecer as animações específicas e definir o comportamento.
 */
public abstract class EngineBarrier extends GameObject implements ISavable {

    protected double health;
    protected double maxHealth;
    protected boolean isDestroyed = false;
    protected Animator animator;
    protected String barrierId;
    protected boolean requiresSpecialWeapon = false;
    protected String requiredWeaponType;
    protected boolean canRegenerate = false;
    protected double regenerationRate = 0.0; // Vida por tick
    protected int regenerationDelay = 0; // Ticks sem dano antes de regenerar
    protected int timeSinceLastDamage = 0;
    protected boolean dropsLoot = false;
    protected String lootTable;

    public EngineBarrier(JSONObject properties) {
        super(properties);
    }

    @Override
    public void initialize(JSONObject properties) {
        super.initialize(properties);
        PropertiesReader reader = new PropertiesReader(properties);
        
        this.maxHealth = reader.getDouble("maxHealth", 100.0);
        this.health = reader.getDouble("health", maxHealth);
        this.barrierId = reader.getString("barrierId", "");
        this.requiresSpecialWeapon = reader.getBoolean("requiresSpecialWeapon", false);
        this.requiredWeaponType = reader.getString("requiredWeaponType", "");
        this.canRegenerate = reader.getBoolean("canRegenerate", false);
        this.regenerationRate = reader.getDouble("regenerationRate", 0.1);
        this.regenerationDelay = reader.getInt("regenerationDelay", 300); // 5 segundos a 60 FPS
        this.dropsLoot = reader.getBoolean("dropsLoot", false);
        this.lootTable = reader.getString("lootTable", "");
        this.isDestroyed = reader.getBoolean("isDestroyed", false);

        this.animator = new Animator();
        this.addComponent(animator);
        
        // Chama o método abstrato que a classe do JOGO irá implementar
        setupAnimations(this.animator);

        // Adiciona zona de interação para ataques manuais (opcional)
        InteractionComponent interaction = new InteractionComponent();
        interaction.addZone(new InteractionZone(this, InteractionZone.TYPE_DIALOGUE, 24.0));
        this.addComponent(interaction);

        updateStateVisuals();
        setCollisionType(isDestroyed ? CollisionType.NO_COLLISION : CollisionType.SOLID);
    }
    
    /**
     * Método principal de interação com a barreira (ataque manual)
     */
    public void interact() {
        if (isDestroyed) return;
        if (!animator.getCurrentAnimationKey().startsWith("idle")) return;

        // Verifica se precisa de arma especial
        if (requiresSpecialWeapon && !hasRequiredWeapon()) {
            onSpecialWeaponRequired();
            return;
        }

        // Aplica dano baseado na arma do jogador
        double damage = getPlayerWeaponDamage();
        takeDamage(damage);
    }

    /**
     * Aplica dano à barreira
     */
    public void takeDamage(double damage) {
        if (isDestroyed || damage <= 0) return;

        // Verifica se o tipo de dano é válido
        if (requiresSpecialWeapon && !isValidDamageType()) {
            onInvalidDamageType();
            return;
        }

        health -= damage;
        timeSinceLastDamage = 0;
        
        // Notifica que a barreira tomou dano
        onBarrierDamaged(damage);

        if (health <= 0) {
            destroyBarrier();
        } else {
            // Toca animação de dano se não foi destruída
            animator.play("damaged");
        }
    }

    /**
     * Destrói a barreira
     */
    protected void destroyBarrier() {
        if (isDestroyed) return;
        
        isDestroyed = true;
        health = 0;
        animator.play("destroying");
        setCollisionType(CollisionType.NO_COLLISION);
        
        // Dropa loot se configurado
        if (dropsLoot && !lootTable.isEmpty()) {
            dropLoot(lootTable);
        }
        
        // Notifica que a barreira foi destruída
        onBarrierDestroyed();
    }

    /**
     * Regenera a barreira (se aplicável)
     */
    protected void regenerateHealth() {
        if (!canRegenerate || isDestroyed) return;
        if (timeSinceLastDamage < regenerationDelay) return;
        
        if (health < maxHealth) {
            health += regenerationRate;
            if (health > maxHealth) {
                health = maxHealth;
            }
            onBarrierRegenerated();
        }
    }

    @Override
    public void tick() {
        super.tick();
        
        if (!isDestroyed) {
            timeSinceLastDamage++;
            regenerateHealth();
        }
        
        // Gerencia as animações
        if (animator.getCurrentAnimation() != null && animator.getCurrentAnimation().hasFinished()) {
            String currentKey = animator.getCurrentAnimationKey();
            
            if ("damaged".equals(currentKey)) {
                updateStateVisuals();
            } else if ("destroying".equals(currentKey)) {
                animator.play("destroyed");
                // Pode marcar para remoção completa do jogo se necessário
                // this.isDestroyed = true;
            }
        }
    }
    
    /**
     * Atualiza os visuais baseado no estado atual
     */
    private void updateStateVisuals() {
        if (isDestroyed) {
            animator.play("destroyed");
            return;
        }
        
        // Diferentes estados visuais baseados na vida
        double healthPercentage = health / maxHealth;
        
        if (healthPercentage > 0.75) {
            animator.play("idleIntact");
        } else if (healthPercentage > 0.5) {
            animator.play("idleDamaged");
        } else if (healthPercentage > 0.25) {
            animator.play("idleHeavilyDamaged");
        } else {
            animator.play("idleCritical");
        }
    }

    @Override
    public JSONObject saveState() {
        JSONObject state = new JSONObject();
        state.put("name", this.name);
        state.put("health", this.health);
        state.put("isDestroyed", this.isDestroyed);
        state.put("timeSinceLastDamage", this.timeSinceLastDamage);
        return state;
    }

    @Override
    public void loadState(JSONObject state) {
        this.health = state.getDouble("health");
        this.isDestroyed = state.getBoolean("isDestroyed");
        this.timeSinceLastDamage = state.getInt("timeSinceLastDamage");
        updateStateVisuals();
        setCollisionType(isDestroyed ? CollisionType.NO_COLLISION : CollisionType.SOLID);
    }
    
    // Getters
    public double getHealth() { return health; }
    public double getMaxHealth() { return maxHealth; }
    public boolean isDestroyed() { return isDestroyed; }
    public String getBarrierId() { return barrierId; }
    public boolean requiresSpecialWeapon() { return requiresSpecialWeapon; }
    public String getRequiredWeaponType() { return requiredWeaponType; }
    public boolean canRegenerate() { return canRegenerate; }
    public double getRegenerationRate() { return regenerationRate; }
    public boolean dropsLoot() { return dropsLoot; }
    public String getLootTable() { return lootTable; }
    public double getHealthPercentage() { return health / maxHealth; }
    
    /**
     * MÉTODOS ABSTRATOS: A classe do jogo DEVE implementar estes métodos
     */
    
    /**
     * Configura as animações específicas da barreira
     * @param animator O componente Animator a ser configurado
     */
    protected abstract void setupAnimations(Animator animator);
    
    /**
     * Verifica se o jogador possui a arma necessária
     * @return true se possui a arma necessária ou se não é necessária
     */
    protected abstract boolean hasRequiredWeapon();
    
    /**
     * Verifica se o tipo de dano atual é válido para esta barreira
     * @return true se o dano é válido
     */
    protected abstract boolean isValidDamageType();
    
    /**
     * Obtém o dano da arma atual do jogador
     * @return O valor do dano a ser aplicado
     */
    protected abstract double getPlayerWeaponDamage();
    
    /**
     * Dropa o loot da barreira destruída
     * @param lootTable A tabela de loot a ser usada
     */
    protected abstract void dropLoot(String lootTable);
    
    /**
     * MÉTODOS OPCIONAIS: A classe do jogo pode sobrescrever para comportamento customizado
     */
    
    /**
     * Chamado quando a barreira toma dano
     */
    protected void onBarrierDamaged(double damage) {
        // Implementação padrão vazia - pode ser sobrescrita
    }
    
    /**
     * Chamado quando a barreira é destruída
     */
    protected void onBarrierDestroyed() {
        // Implementação padrão vazia - pode ser sobrescrita
    }
    
    /**
     * Chamado quando a barreira regenera vida
     */
    protected void onBarrierRegenerated() {
        // Implementação padrão vazia - pode ser sobrescrita
    }
    
    /**
     * Chamado quando o jogador tenta atacar sem a arma necessária
     */
    protected void onSpecialWeaponRequired() {
        // Implementação padrão vazia - pode ser sobrescrita
    }
    
    /**
     * Chamado quando o tipo de dano não é válido
     */
    protected void onInvalidDamageType() {
        // Implementação padrão vazia - pode ser sobrescrita
    }
}
