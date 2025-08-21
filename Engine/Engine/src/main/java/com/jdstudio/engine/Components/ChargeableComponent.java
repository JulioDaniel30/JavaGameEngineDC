package com.jdstudio.engine.Components;

import java.util.function.Consumer;
import com.jdstudio.engine.Object.GameObject;

public class ChargeableComponent extends Component {
    
    private float chargeTime; // Tempo total para carregar (em ticks)
    private float currentCharge = 0;
    private boolean isCharging = false;
    private Consumer<GameObject> onChargedAction; // Ação a ser executada quando o carregamento termina

    public ChargeableComponent(int chargeDurationInTicks, Consumer<GameObject> onChargedAction) {
        this.chargeTime = chargeDurationInTicks;
        this.onChargedAction = onChargedAction;
    }

    @Override
    public void update() {
        if (!isCharging) return;
        
        currentCharge++;
        if (currentCharge >= chargeTime) {
            isCharging = false;
            currentCharge = 0;
            if (onChargedAction != null) {
                onChargedAction.accept(owner); // Executa a ação final
            }
        }
    }

    public void startCharging() {
        if (!isCharging) {
            this.isCharging = true;
            this.currentCharge = 0;
        }
    }
    
    public void stopCharging() { this.isCharging = false; this.currentCharge = 0; }
    public boolean isCharging() { return this.isCharging; }
    public float getProgress() {
        if (chargeTime <= 0) return 0;
        return currentCharge / chargeTime;
    }
}