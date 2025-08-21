// engine
package com.jdstudio.engine.States;

import java.awt.Graphics;

/**
 * A base abstrata e fundamental para todos os estados do jogo.
 * Define o contrato mínimo que um estado de jogo deve cumprir.
 */
public abstract class GameState {

    /**
     * Chamado a cada quadro para atualizar a lógica do estado.
     */
    public abstract void tick();

    /**
     * Chamado a cada quadro para renderizar os elementos visuais do estado.
     * @param g O contexto gráfico para desenhar.
     */
    public abstract void render(Graphics g);

    /**
     * (Opcional) Chamado quando o estado é definido como o atual.
     * Útil para inicializar recursos.
     */
    public void onEnter() {}

    /**
     * (Opcional) Chamado quando o estado está prestes a ser trocado.
     * Útil para liberar recursos.
     */
    public void onExit() {}

	
}