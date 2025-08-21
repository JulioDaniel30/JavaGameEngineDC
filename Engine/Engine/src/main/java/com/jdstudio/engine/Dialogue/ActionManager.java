package com.jdstudio.engine.Dialogue;

import java.util.HashMap;
import java.util.Map;
import com.jdstudio.engine.Object.GameObject;

public class ActionManager {

    private static final ActionManager instance = new ActionManager();
    private final Map<String, DialogueAction> registeredActions = new HashMap<>();

    private ActionManager() {}

    public static ActionManager getInstance() {
        return instance;
    }

    /**
     * O Jogo usa este método para registrar suas ações customizadas.
     * @param key A chave de texto da ação (ex: "start_quest").
     * @param action A implementação da ação.
     */
    public void registerAction(String key, DialogueAction action) {
        registeredActions.put(key, action);
    }

    /**
     * A Engine (DialogueManager) usa este método para executar uma ação.
     * @param key A chave da ação a ser executada.
     */
    public void executeAction(String key, GameObject interactor, GameObject source) {
        if (registeredActions.containsKey(key)) {
            registeredActions.get(key).execute(interactor, source);
        } else {
            System.err.println("Aviso: Ação de diálogo não registrada: '" + key + "'");
        }
    }
}