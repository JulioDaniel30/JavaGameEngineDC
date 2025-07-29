// engine
package com.JDStudio.Engine.Dialogue;

import java.util.ArrayList;
import java.util.List;

public class DialogueNode {
    public final int id;
    public final String speakerName;
    public final String text;
    public final List<DialogueChoice> choices;

    public DialogueNode(int id, String speakerName, String text) {
        this.id = id;
        this.speakerName = speakerName;
        this.text = text;
        this.choices = new ArrayList<>();
    }

    public void addChoice(String choiceText, int nextNodeId, String action) {
        this.choices.add(new DialogueChoice(choiceText, nextNodeId, action));
    }
}