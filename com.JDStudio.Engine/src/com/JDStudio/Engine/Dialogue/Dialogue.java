// engine
package com.JDStudio.Engine.Dialogue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.JDStudio.Engine.Object.GameObject; // Importação necessária

public class Dialogue {
    private final Map<Integer, DialogueNode> nodes = new HashMap<>();
    private final int defaultEntryPointId;
    private final List<DialogueEntryPoint> entryPoints;

    public Dialogue(int defaultEntryPointId) {
        this.defaultEntryPointId = defaultEntryPointId;
        this.entryPoints = new ArrayList<>();
    }

    public void addNode(DialogueNode node) { this.nodes.put(node.id, node); }
    public void addEntryPoint(DialogueEntryPoint entryPoint) { this.entryPoints.add(entryPoint); }
    public DialogueNode getNode(int id) { return nodes.get(id); }
    public Map<Integer, DialogueNode> getNodes() { return nodes; }

    /**
     * **MÉTODO CORRIGIDO**: Determina o nó inicial correto com base no estado do jogo.
     * Agora, ele usa o ConditionManager para verificar as condições.
     * @param interactor O GameObject que está a interagir (o jogador).
     */
    public DialogueNode getStartNode(GameObject interactor) {
        ConditionManager conditionManager = ConditionManager.getInstance();
        
        // Verifica os pontos de entrada condicionais primeiro
        for (DialogueEntryPoint entry : entryPoints) {
            // Delega a verificação da condição para o ConditionManager
            if (conditionManager.checkCondition(entry.condition(), interactor)) {
                return getNode(entry.nodeId());
            }
        }
        
        // Se nenhuma condição for satisfeita, usa o padrão
        return getNode(defaultEntryPointId);
    }
}