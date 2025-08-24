package com.jdstudio.engine.Dialogue;
import com.jdstudio.engine.Object.GameObject;

/**
 * A functional interface for actions that can be triggered by dialogue choices or nodes.
 * This allows for creating custom, game-specific logic (like starting a quest or giving an item)
 * that can be executed during a conversation.
 * 
 * @author JDStudio
 */
@FunctionalInterface
public interface DialogueAction {
    /**
     * Executes the logic of the action.
     * 
     * @param interactor The GameObject that initiated the interaction (usually the Player).
     * @param source     The GameObject that is the source of the dialogue (usually the NPC).
     */
    void execute(GameObject interactor, GameObject source);
}
