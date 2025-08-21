package com.jdstudio.engine.Dialogue;
import com.jdstudio.engine.Object.GameObject;
/**
 * Interface para ações que podem ser acionadas por escolhas de diálogo.
 */
@FunctionalInterface
public interface DialogueAction {
    /**
     * Executa a lógica da ação.
     * @param interactor O GameObject que iniciou a interação (geralmente o Player).
     * @param source     O GameObject que é a fonte do diálogo (geralmente o NPC).
     */
    void execute(GameObject interactor, GameObject source);
}