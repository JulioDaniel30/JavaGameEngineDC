// engine
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
    // Construtor privado para garantir que seja um Singleton
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
        this.currentNode = dialogue.getStartNode();
        this.isActive = true;
        this.dialogueSource = source;
        this.interactor = interactor; // Guarda a referência do jogador
    }

    /**
     * Finaliza a conversa atual.
     */
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
        if (!isActive || currentNode == null || currentNode.choices.isEmpty()) {
            // Se não há escolhas, qualquer ação finaliza o diálogo
            endDialogue();
            return;
        }

        if (choiceIndex >= 0 && choiceIndex < currentNode.choices.size()) {
        	DialogueChoice choice = currentNode.choices.get(choiceIndex);
        	
        	// --- EXECUTA A AÇÃO ANTES DE AVANÇAR ---
            if (choice.action != null && !choice.action.isEmpty()) {
                // Passa o jogador (que está interagindo) e o NPC (fonte do diálogo)
            	ActionManager.getInstance().executeAction(choice.action, this.interactor, this.dialogueSource);
            }
        	
            int nextNodeId = choice.nextNodeId;

            if (nextNodeId == -1) { // -1 é o nosso sinal para terminar a conversa
                endDialogue();
            } else {
                currentNode = currentDialogue.getNode(nextNodeId);
                System.out.println("Avançando para o nó #" + currentNode.id);
            }
        }
    }
    
    // --- Getters para a UI poder ler o estado atual ---

    public boolean isActive() {
        return isActive;
    }

    public DialogueNode getCurrentNode() {
        return currentNode;
    }
}