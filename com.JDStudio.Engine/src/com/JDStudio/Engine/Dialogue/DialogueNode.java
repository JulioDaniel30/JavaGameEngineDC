package com.JDStudio.Engine.Dialogue;

import java.util.ArrayList;
import java.util.List;

public class DialogueNode {
    public final int id;
    public final String speakerName;
    public final String text;
    private final List<DialogueChoice> choices; // Alterado para private para melhor encapsulamento

    public DialogueNode(int id, String speakerName, String text) {
        this.id = id;
        this.speakerName = speakerName;
        this.text = text;
        this.choices = new ArrayList<>();
    }

    /**
     * **MÉTODO ATUALIZADO**
     * Agora aceita o parâmetro 'condition' e o passa para o construtor de DialogueChoice.
     */
    public void addChoice(String choiceText, int nextNodeId, String action, String condition) {
        this.choices.add(new DialogueChoice(choiceText, nextNodeId, action, condition));
    }

    /**
     * Retorna a lista de escolhas para ser usada pela engine.
     * @return Uma lista de DialogueChoice.
     */
    public List<DialogueChoice> getChoices() {
        return this.choices;
    }
}