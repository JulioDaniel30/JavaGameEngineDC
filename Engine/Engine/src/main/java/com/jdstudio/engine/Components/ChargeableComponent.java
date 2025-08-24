package com.jdstudio.engine.Components;

import java.util.function.Consumer;
import com.jdstudio.engine.Object.GameObject;

/**
 * A component that allows a GameObject to have a "chargeable" action.
 * This can be used for abilities that require a wind-up time before executing.
 * The component tracks the charge progress and executes a callback action when fully charged.
 * 
 * @author JDStudio
 */
public class ChargeableComponent extends Component {
    
    /** The total time required to fully charge, in game ticks. */
    private float chargeTime;
    
    /** The current charge progress, in game ticks. */
    private float currentCharge = 0;
    
    /** Flag indicating if the component is currently charging. */
    private boolean isCharging = false;
    
    /** The action to be executed when the charge is complete. */
    private Consumer<GameObject> onChargedAction;

    /**
     * Constructs a new ChargeableComponent.
     *
     * @param chargeDurationInTicks The total time, in game ticks, required to fully charge.
     * @param onChargedAction       The action to execute when the charge is complete. 
     *                              The owner GameObject is passed as a parameter to this action.
     */
    public ChargeableComponent(int chargeDurationInTicks, Consumer<GameObject> onChargedAction) {
        this.chargeTime = chargeDurationInTicks;
        this.onChargedAction = onChargedAction;
    }

    /**
     * Updates the charging progress. If charging, it increments the current charge
     * and executes the action when fully charged.
     */
    @Override
    public void update() {
        if (!isCharging) return;
        
        currentCharge++;
        if (currentCharge >= chargeTime) {
            isCharging = false;
            currentCharge = 0;
            if (onChargedAction != null) {
                onChargedAction.accept(owner); // Execute the final action
            }
        }
    }

    /**
     * Starts the charging process. If already charging, it does nothing.
     */
    public void startCharging() {
        if (!isCharging) {
            this.isCharging = true;
            this.currentCharge = 0;
        }
    }
    
    /**
     * Stops the charging process immediately and resets the progress.
     */
    public void stopCharging() { 
        this.isCharging = false; 
        this.currentCharge = 0; 
    }
    
    /**
     * Checks if the component is currently in the process of charging.
     *
     * @return true if charging, false otherwise.
     */
    public boolean isCharging() { 
        return this.isCharging; 
    }
    
    /**
     * Gets the current charge progress as a normalized value between 0.0 and 1.0.
     *
     * @return The charge progress (0.0 for empty, 1.0 for full).
     */
    public float getProgress() {
        if (chargeTime <= 0) return 0;
        return currentCharge / chargeTime;
    }
}
