package com.jdstudio.engine.Dialogue;
import java.util.HashMap;
import java.util.Map;
import com.jdstudio.engine.Object.GameObject;

/**
 * A singleton manager that registers and checks custom dialogue conditions.
 * This allows the game to define specific conditions (e.g., player has a certain item, a quest is active)
 * that can be used to control the flow of a conversation.
 * 
 * @author JDStudio
 */
public class ConditionManager {
    private static final ConditionManager instance = new ConditionManager();
    private final Map<String, DialogueCondition> registeredConditions = new HashMap<>();

    private ConditionManager() {}

    /**
     * Gets the single instance of the ConditionManager.
     * @return The singleton instance.
     */
    public static ConditionManager getInstance() { 
        return instance; 
    }

    /**
     * The Game uses this method to register its custom condition logic.
     * @param key     The key for the condition (e.g., "HAS_WOLF_PELT").
     * @param checker The implementation of the checking logic.
     */
    public void registerCondition(String key, DialogueCondition checker) {
        registeredConditions.put(key, checker);
    }

    /**
     * The Engine (dialogue system) uses this method to check if a condition is met.
     * @param key        The key of the condition to check.
     * @param interactor The GameObject that is interacting (e.g., the player).
     * @return true if the condition is met, false otherwise.
     */
    public boolean checkCondition(String key, GameObject interactor) {
        if (registeredConditions.containsKey(key)) {
            // Execute the checking logic that was registered by the game
            return registeredConditions.get(key).check(interactor);
        }
        // If the condition was not registered, assume it cannot be met.
        System.err.println("Warning: Unregistered dialogue condition: '" + key + "'");
        return false;
    }
}
