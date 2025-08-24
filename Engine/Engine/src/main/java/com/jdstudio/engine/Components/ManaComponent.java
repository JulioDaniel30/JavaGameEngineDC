package com.jdstudio.engine.Components;

/**
 * A component that manages the mana or energy of a GameObject.
 * It tracks current and maximum mana, and provides methods for using and restoring it.
 * 
 * @author JDStudio
 */
public class ManaComponent extends Component {
    
    /** The current mana of the GameObject. */
    public int currentMana;
    
    /** The maximum mana of the GameObject. */
    public int maxMana;

    /**
     * Constructs a new ManaComponent.
     *
     * @param maxMana The maximum mana value.
     */
    public ManaComponent(int maxMana) {
        this.maxMana = maxMana;
        this.currentMana = maxMana;
    }

    /**
     * Reduces the current mana by the specified amount.
     * Mana will not go below zero.
     *
     * @param amount The amount of mana to use.
     */
    public void useMana(int amount) {
        this.currentMana -= amount;
        if (this.currentMana < 0) this.currentMana = 0;
    }

    /**
     * Increases the current mana by the specified amount.
     * Mana will not go above the maximum mana.
     *
     * @param amount The amount of mana to restore.
     */
    public void restoreMana(int amount) {
        this.currentMana += amount;
        if (this.currentMana > this.maxMana) this.currentMana = this.maxMana;
    }

    /**
     * Calculates the mana percentage.
     *
     * @return The current mana as a percentage of the maximum mana (from 0.0 to 1.0).
     */
    public float getManaPercentage() {
        if (maxMana <= 0) return 0;
        return (float) currentMana / (float) maxMana;
    }

    /**
     * Checks if there is enough mana for an action.
     *
     * @param amount The amount of mana required.
     * @return true if current mana is greater than or equal to the amount, false otherwise.
     */
    public boolean hasEnoughMana(int amount) {
        return this.currentMana >= amount;
    }
}
