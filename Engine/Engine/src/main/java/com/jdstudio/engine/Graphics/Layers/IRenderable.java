package com.jdstudio.engine.Graphics.Layers;

import java.awt.Graphics;

public interface IRenderable {
    /**
     * Desenha o objeto.
     */
    void render(Graphics g);

    /**
     * Retorna a camada de renderização na qual este objeto deve ser desenhado.
     */
    RenderLayer getRenderLayer();

    /**
     * Retorna a profundidade específica do objeto dentro da sua camada (z-index).
     * Objetos com um valor de 'z' maior são desenhados por cima dentro da mesma camada.
     * Por padrão, a posição Y do objeto é usada.
     */
    default int getZOrder() {
        return 0; // Padrão
    }
    
    /**
     * Verifica se o objeto deve ser renderizado.
     */
    boolean isVisible();
}