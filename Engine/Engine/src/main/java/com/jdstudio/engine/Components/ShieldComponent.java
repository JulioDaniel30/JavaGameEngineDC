package com.jdstudio.engine.Components;

import com.jdstudio.engine.Object.GameObject;
import java.util.function.Consumer;

/**
 * A component that provides a regenerating shield for a GameObject.
 * The shield has its own health pool and can absorb damage.
 * It uses a ChargeableComponent to manage the delay before the shield starts recharging.
 * 
 * @author JDStudio
 */
public class ShieldComponent extends Component {

    /** The current health of the shield. */
    private float shieldHealth;
    
    /** The maximum health of the shield. */
    private final float maxShieldHealth;
    
    /** The component that handles the recharge delay. */
    private final ChargeableComponent chargeableComponent;
    
    /** Flag indicating if the shield is currently active and can absorb damage. */
    private boolean isActive = true;

    /**
     * Constructs a new ShieldComponent.
     *
     * @param maxShieldHealth The maximum health of the shield.
     * @param rechargeDelayInTicks The delay in game ticks before the shield starts to recharge after taking damage.
     * @param onRechargedAction An optional action to execute when the shield is fully recharged.
     */
    public ShieldComponent(float maxShieldHealth, int rechargeDelayInTicks, Consumer<GameObject> onRechargedAction) {
        this.maxShieldHealth = maxShieldHealth;
        this.shieldHealth = maxShieldHealth;
        
        // The action to perform when the charge (recharge delay) is complete.
        Consumer<GameObject> rechargeAction = (owner) -> {
            this.shieldHealth = this.maxShieldHealth; // Restore shield to full health
            if (onRechargedAction != null) {
                onRechargedAction.accept(owner);
            }
        };

        this.chargeableComponent = new ChargeableComponent(rechargeDelayInTicks, rechargeAction);
    }

    /**
     * Reduces the shield's health by the specified amount.
     * If the shield is active, it will absorb the damage.
     * Taking damage will stop any current recharge process and restart the recharge delay.
     *
     * @param damage The amount of damage to inflict on the shield.
     */
    public void takeDamage(float damage) {
        if (!isActive) return;

        this.shieldHealth -= damage;
        chargeableComponent.stopCharging(); // Stop any ongoing recharge

        if (this.shieldHealth <= 0) {
            this.shieldHealth = 0;
            this.isActive = false; // Shield is broken
        }
        
        // Start the timer to recharge after taking damage
        chargeableComponent.startCharging();
    }

    /**
     * Manually starts the shield recharge process, after the configured delay.
     */
    public void startRecharge() {
        chargeableComponent.startCharging();
    }

    /**
     * Gets the current health of the shield.
     * @return The current shield health.
     */
    public float getShieldHealth() {
        return shieldHealth;
    }

    /**
     * Gets the maximum health of the shield.
     * @return The maximum shield health.
     */
    public float getMaxShieldHealth() {
        return maxShieldHealth;
    }

    /**
     * Checks if the shield is currently in its recharge delay phase.
     * @return true if the recharge delay timer is active, false otherwise.
     */
    public boolean isRecharging() {
        return chargeableComponent.isCharging();
    }

    /**
     * Updates the component, primarily by updating the internal chargeable component.
     */
    @Override
    public void update() {
        // The chargeable component handles the recharge delay and action.
        chargeableComponent.update();
    }

    /**
     * Checks if the shield is currently active (i.e., has health and can absorb damage).
     * @return true if the shield is active, false if it is broken or disabled.
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Manually sets the active state of the shield.
     * @param active true to activate, false to deactivate.
     */
    public void setActive(boolean active) {
        isActive = active;
        if (active && this.shieldHealth == 0) {
            this.shieldHealth = this.maxShieldHealth; // Restore health if reactivated
        }
    }

    /**
     * Gets the internal ChargeableComponent instance.
     * @return The ChargeableComponent used for the recharge delay.
     */
    public ChargeableComponent getChargeableComponent() {
        return chargeableComponent;
    }
}
