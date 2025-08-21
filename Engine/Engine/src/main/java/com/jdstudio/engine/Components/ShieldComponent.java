package com.jdstudio.engine.Components;

import com.jdstudio.engine.Object.GameObject;
import java.util.function.Consumer;


// This class represents a shield component that can be attached to a game object.
// this component manages the shield's health, recharge, and other related functionalities.
// using chargeable component to manage shield recharging.
public class  ShieldComponent extends Component {

    private float shieldHealth;
    private float maxShieldHealth;
    private ChargeableComponent chargeableComponent;
    private boolean isRecharging = false;
    private boolean isActive = true;

    public ShieldComponent(float maxShieldHealth, int rechargeDurationInTicks, Consumer<GameObject> onChargedAction) {
        this.maxShieldHealth = maxShieldHealth;
        this.shieldHealth = maxShieldHealth; // Inicializa o escudo com a saúde máxima
        this.isActive = true; // O escudo está ativo por padrão
        this.isRecharging = false; // O escudo não está recarregando inicialmente
        this.chargeableComponent = new ChargeableComponent(rechargeDurationInTicks, onChargedAction);
    }

    public void takeDamage(float damage) {
        shieldHealth -= damage;
        if (shieldHealth < 0) shieldHealth = 0;
    }

    public void recharge() {
        chargeableComponent.startCharging();
    }

    public float getShieldHealth() {
        return shieldHealth;
    }

    public float getMaxShieldHealth() {
        return maxShieldHealth;
    }

    public boolean isCharging() {
        return chargeableComponent.isCharging();
    }

    @Override
    public void update() {
        if (isRecharging) {
            chargeableComponent.update();
            if (!chargeableComponent.isCharging() && shieldHealth < maxShieldHealth) {
                shieldHealth += 1; // Recarrega o escudo gradualmente
                if (shieldHealth > maxShieldHealth) shieldHealth = maxShieldHealth;
            }
        }
        if (shieldHealth <= 0) {
            isActive = false; // Desativa o escudo se a saúde chegar a zero
        }
        if (chargeableComponent.isCharging()) {
            isRecharging = true; // O escudo está recarregando
        } else {
            isRecharging = false; // O escudo não está recarregando
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
    public ChargeableComponent getChargeableComponent() {
        return chargeableComponent;
    }

}