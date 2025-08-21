package com.jdstudio.engine.Dialogue;

import com.jdstudio.engine.Object.GameObject;

/**
 * Uma interface funcional que representa um "verificador" de condição de diálogo.
 * A sua única responsabilidade é retornar true ou false com base no estado do jogo.
 */
@FunctionalInterface
public interface DialogueCondition {
    /**
     * Verifica se uma condição foi satisfeita.
     * @param interactor O GameObject que está a interagir (geralmente o Player).
     * @return true se a condição for satisfeita, false caso contrário.
     */
    boolean check(GameObject interactor);
}