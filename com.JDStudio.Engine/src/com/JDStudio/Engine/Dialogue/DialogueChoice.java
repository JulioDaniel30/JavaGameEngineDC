package com.JDStudio.Engine.Dialogue;

public class DialogueChoice {
    public final String text;
    public final int nextNodeId;
    public final String action;
    public final String condition; // O novo campo

    /**
     * **CONSTRUTOR ATUALIZADO**
     * Agora aceita os quatro parâmetros, incluindo a condição opcional.
     */
    public DialogueChoice(String text, int nextNodeId, String action, String condition) {
        this.text = text;
        this.nextNodeId = nextNodeId;
        this.action = action;
        this.condition = condition;
    }
    
    /**
     * Retorna a condição para esta escolha, ou null se não houver nenhuma.
     * @return A string da condição.
     */
    public String getCondition() {
        return this.condition;
    }
}