package com.JDStudio.Engine.Graphics.UI;

import java.awt.Graphics;

/**
 * Representa a base abstrata para todos os elementos de interface de usuário (UI).
 * <p>
 * Esta classe estabelece a estrutura fundamental que todo elemento de UI deve ter,
 * como posição, dimensões e visibilidade. Ela também define o contrato de que
 * todo elemento deve ser capaz de se renderizar na tela.
 *
 * @author JDStudio
 * @since 1.0
 */
public abstract class UIElement {

    /** A coordenada horizontal do elemento (eixo X). */
    protected int x;

    /** A coordenada vertical do elemento (eixo Y). */
    protected int y;

    /** A largura do elemento em pixels. */
    protected int width;

    /** A altura do elemento em pixels. */
    protected int height;

    /** Controla se o elemento será renderizado. */
    protected boolean visible = true;

    /**
     * Construtor base para um elemento de UI.
     *
     * @param x A posição horizontal inicial.
     * @param y A posição vertical inicial.
     */
    public UIElement(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Método abstrato que as subclasses devem implementar para desenhar o elemento.
     * <p>
     * Este método é chamado pelo motor gráfico a cada quadro (frame) para
     * que o elemento se desenhe na tela.
     *
     * @param g O contexto {@link Graphics} usado para as operações de desenho.
     */
    public abstract void render(Graphics g);

    /**
     * Define se o elemento deve ser visível e renderizado.
     *
     * @param visible {@code true} para tornar visível, {@code false} para ocultar.
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Verifica se o elemento está atualmente visível.
     *
     * @return {@code true} se o elemento estiver visível, {@code false} caso contrário.
     */
    public boolean isVisible() {
        return this.visible;
    }

    /**
     * Atualiza a posição do elemento na tela.
     *
     * @param x A nova coordenada horizontal (eixo X).
     * @param y A nova coordenada vertical (eixo Y).
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}