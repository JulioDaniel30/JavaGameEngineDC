package com.jdstudio.engine.Dialogue;

import java.util.HashMap;
import java.util.Map;
import com.jdstudio.engine.Object.GameObject;

/**
 * A singleton manager for registering and executing custom dialogue actions.
 * This allows the game to define specific actions (e.g., starting a quest, giving an item)
 * that can be triggered from a dialogue file.
 * 
 * @author JDStudio
 */
public class ActionManager {

    private static final ActionManager instance = new ActionManager();
    private final Map<String, DialogueAction> registeredActions = new HashMap<>();
    
    private ActionManager() {}

    /**
     * Gets the single instance of the ActionManager.
     * @return The singleton instance.
     */
    public static ActionManager getInstance() {
        return instance;
    }

    /**
     * The Game uses this method to register its custom actions.
     * @param key    The text key of the action (e.g., "start_quest").
     * @param action The implementation of the action.
     */
    public void registerAction(String key, DialogueAction action) {
        registeredActions.put(key, action);
    }

    /**
     * The Engine (DialogueManager) uses this method to execute an action.
     * @param key        The key of the action to be executed.
     * @param interactor The GameObject that initiated the dialogue (e.g., the player).
     * @param source     The GameObject that is the source of the dialogue (e.g., the NPC).
     */
    public void executeAction(String key, GameObject interactor, GameObject source) {
        if (registeredActions.containsKey(key)) {
            registeredActions.get(key).execute(interactor, source);
        } else {
            System.err.println("Warning: Unregistered dialogue action: '" + key + "'");
        }
    }
}
