package com.jdstudio.engine.Components;

public class ManaComponent extends Component {
    
    public int currentMana;
    public int maxMana;

    public ManaComponent(int maxMana) {
        this.maxMana = maxMana;
        this.currentMana = maxMana;
    }

    public void useMana(int amount) {
        this.currentMana -= amount;
        if (this.currentMana < 0) this.currentMana = 0;
    }

    public void restoreMana(int amount) {
        this.currentMana += amount;
        if (this.currentMana > this.maxMana) this.currentMana = this.maxMana;
    }

    public float getManaPercentage() {
        if (maxMana <= 0) return 0;
        return (float) currentMana / (float) maxMana;
    }
}