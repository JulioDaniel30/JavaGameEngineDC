// engine
package com.JDStudio.Engine.Object;

import com.JDStudio.Engine.Dialogue.Dialogue;
import com.JDStudio.Engine.Dialogue.DialogueManager;
import com.JDStudio.Engine.Graphics.Sprite.Sprite;

/**
 * Uma classe base para Personagens Não-Jogáveis (NPCs) na engine.
 * Fornece uma base pronta para interação e diálogo.
 */
public abstract class EngineNPC extends Character implements Interactable {

    protected Dialogue dialogue;
    protected int interactionRadius = 24; // Raio de interação padrão

    public EngineNPC(double x, double y, int width, int height) {
        super(x, y, width, height);
    }

    /**
     * Define o diálogo que este NPC usará.
     * @param dialogue O objeto de diálogo.
     */
    public void setDialogue(Dialogue dialogue) {
        this.dialogue = dialogue;
    }

    /**
     * Define o sprite visual para este NPC.
     * @param sprite O sprite.
     */
    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }
    
    /**
     * Define o raio de interação para este NPC.
     * @param radius O raio em pixels.
     */
    public void setInteractionRadius(int radius) {
        this.interactionRadius = radius;
    }

    /**
     * Lógica de interação padrão: se houver um diálogo, inicie-o.
     */
    @Override
    public void onInteract(GameObject source) { // 'source' aqui é o jogador
        if (dialogue != null && !DialogueManager.getInstance().isActive()) {
            // Passa o diálogo, o próprio NPC (this) como fonte, e o jogador (source) como interator
            DialogueManager.getInstance().startDialogue(dialogue, this, source);
        }
    }

    @Override
    public int getInteractionRadius() {
        return this.interactionRadius;
    }

    @Override
    public void tick() {
        super.tick();
        // A lógica de IA de um NPC (se houver, como olhar para o jogador) viria aqui.
        // Por padrão, eles ficam parados.
        movement.tick(); // Garante que o componente de movimento seja atualizado (mesmo que parado)
    }
}