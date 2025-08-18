package com.JDStudio.Engine.Dialogue;

import java.util.HashMap;
import java.util.Map;
import com.JDStudio.Engine.Object.GameObject;

/**
 * Um Singleton que regista e verifica as condições de diálogo customizadas do jogo.
 */
public class ConditionManager {
    private static final ConditionManager instance = new ConditionManager();
    private final Map<String, DialogueCondition> registeredConditions = new HashMap<>();

    private ConditionManager() {}
    public static ConditionManager getInstance() { return instance; }

    /**
     * O Jogo usa este método para registar as suas lógicas de condição.
     * @param key A chave da condição (ex: "TEM_PELE_DE_LOBO").
     * @param checker A implementação da lógica de verificação.
     */
    public void registerCondition(String key, DialogueCondition checker) {
        registeredConditions.put(key, checker);
    }

    /**
     * A Engine (diálogo) usa este método para verificar se uma condição foi satisfeita.
     * @param key A chave da condição a ser verificada.
     * @param interactor O GameObject que está a interagir (o jogador).
     * @return true se a condição for satisfeita, false caso contrário.
     */
    public boolean checkCondition(String key, GameObject interactor) {
        if (registeredConditions.containsKey(key)) {
            // Executa a lógica de verificação que foi registada pelo jogo
            return registeredConditions.get(key).check(interactor);
        }
        // Se a condição não foi registada, assume-se que não pode ser satisfeita.
        System.err.println("Aviso: Condição de diálogo não registada: '" + key + "'");
        return false;
    }
}