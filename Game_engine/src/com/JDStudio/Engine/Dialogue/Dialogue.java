// engine
package com.JDStudio.Engine.Dialogue;

import java.util.HashMap;
import java.util.Map;

public class Dialogue {
    private final Map<Integer, DialogueNode> nodes = new HashMap<>();
    private final int startNodeId;

    public Dialogue(int startNodeId) {
        this.startNodeId = startNodeId;
    }

    public void addNode(DialogueNode node) {
        this.nodes.put(node.id, node);
    }

    public DialogueNode getNode(int id) {
        return nodes.get(id);
    }

    public DialogueNode getStartNode() {
        return getNode(startNodeId);
    }
}