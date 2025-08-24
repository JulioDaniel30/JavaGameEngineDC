package com.jdstudio.engine.Components;

/**
 * A component that manages the health of a GameObject.
 * It tracks current and maximum health, and provides methods for taking damage and healing.
 * 
 * @author JDStudio
 */
public class HealthComponent extends Component {
    
    /** The current health of the GameObject. */
    public int currentHealth;
    
    /** The maximum health of the GameObject. */
    public int maxHealth;

    /**
     * Constructs a new HealthComponent.
     *
     * @param maxHealth The maximum health value.
     */
    public HealthComponent(int maxHealth) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
    }

    /**
     * Reduces the current health by the specified amount.
     * Health will not go below zero.
     *
     * @param amount The amount of damage to take.
     */
    public void takeDamage(int amount) {
        this.currentHealth -= amount;
        if (this.currentHealth < 0) this.currentHealth = 0;
    }

    /**
     * Increases the current health by the specified amount.
     * Health will not go above the maximum health.
     *
     * @param amount The amount of health to restore.
     */
    public void heal(int amount) {
        this.currentHealth += amount;
        if (this.currentHealth > this.maxHealth) this.currentHealth = this.maxHealth;
    }

    /**
     * Calculates the health percentage.
     *
     * @return The current health as a percentage of the maximum health (from 0.0 to 1.0).
     */
    public float getHealthPercentage() {
        if (maxHealth <= 0) return 0;
        return (float) currentHealth / (float) maxHealth;
    }
    
    /**
     * Checks if the GameObject has zero health.
     *
     * @return true if current health is zero, false otherwise.
     */
    public boolean isDead() {
        return this.currentHealth <= 0;
    }
}
