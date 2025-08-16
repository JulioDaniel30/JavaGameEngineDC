package com.JDStudio.Engine.Components;

public class HealthComponent extends Component {
    
    public int currentHealth;
    public int maxHealth;

    public HealthComponent(int maxHealth) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
    }

    public void takeDamage(int amount) {
        this.currentHealth -= amount;
        if (this.currentHealth < 0) this.currentHealth = 0;
    }

    public void heal(int amount) {
        this.currentHealth += amount;
        if (this.currentHealth > this.maxHealth) this.currentHealth = this.maxHealth;
    }

    public float getHealthPercentage() {
        if (maxHealth <= 0) return 0;
        return (float) currentHealth / (float) maxHealth;
    }
}