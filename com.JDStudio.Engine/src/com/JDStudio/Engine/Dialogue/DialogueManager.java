package com.JDStudio.Engine.Dialogue;

import com.JDStudio.Engine.Object.GameObject;

/**
 * Gerencia o estado e o fluxo de uma conversa ativa.
 * Atua como um Singleton para ser acessível globalmente.
 */
public class DialogueManager {

    private static final DialogueManager instance = new DialogueManager();

    private Dialogue currentDialogue;
    private DialogueNode currentNode;
    private boolean isActive = false;
    
    private GameObject dialogueSource; // Quem é o dono do diálogo (o NPC)
    private GameObject interactor;     // Quem está interagindo (o Player)

    private DialogueManager() {}

    public static DialogueManager getInstance() {
        return instance;
    }

    /**
     * Inicia uma nova conversa.
     * @param dialogue O objeto Dialogue a ser iniciado.
     * @param source O GameObject que é a fonte do diálogo (o NPC).
     * @param interactor O GameObject que iniciou a interação (o Player).
     */
    public void startDialogue(Dialogue dialogue, GameObject source, GameObject interactor) {
        this.currentDialogue = dialogue;
        
        this.currentNode = dialogue.getStartNode(interactor);
        
        this.isActive = true;
        this.dialogueSource = source;
        this.interactor = interactor;
    }

    public void endDialogue() {
        this.currentDialogue = null;
        this.currentNode = null;
        this.isActive = false;
        System.out.println("Diálogo finalizado.");
    }

    /**
     * Processa a escolha do jogador e avança para o próximo nó do diálogo.
     * @param choiceIndex O índice da opção escolhida pelo jogador (0, 1, 2...).
     */
    public void selectChoice(int choiceIndex) {
        // --- CÓDIGO CORRIGIDO ---
        if (!isActive || currentNode == null || currentNode.getChoices().isEmpty()) {
            endDialogue();
            return;
        }

        if (choiceIndex >= 0 && choiceIndex < currentNode.getChoices().size()) {
        	DialogueChoice choice = currentNode.getChoices().get(choiceIndex);
        	
            if (choice.action != null && !choice.action.isEmpty()) {
            	ActionManager.getInstance().executeAction(choice.action, this.interactor, this.dialogueSource);
            }
        	
            int nextNodeId = choice.nextNodeId;

            if (nextNodeId == -1) {
                endDialogue();
            } else {
                currentNode = currentDialogue.getNode(nextNodeId);
                if (currentNode == null) {
                    System.err.println("Erro de diálogo: Nó com id " + nextNodeId + " não encontrado. A terminar diálogo.");
                    endDialogue();
                } else {
                    System.out.println("Avançando para o nó #" + currentNode.id);
                }
            }
        }
    }
    
    // --- Getters ---

    public boolean isActive() {
        return isActive;
    }

    public DialogueNode getCurrentNode() {
        return currentNode;
    }

    public void reset() {
        endDialogue();
        System.out.println("DialogueManager resetado.");
    }
}