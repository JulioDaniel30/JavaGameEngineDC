package com.jdstudio.engine.Components;

/**
 * Um componente de dados que guarda a informação para um prompt de interação.
 * (ex: "[E] Falar", "[F] Abrir").
 */
public class InteractionPromptComponent extends Component {
    
    public final String promptText;

    /**
     * @param promptText O texto a ser exibido quando o jogador está próximo.
     */
    public InteractionPromptComponent(String promptText) {
        this.promptText = promptText;
    }
}