package com.JDStudio.Engine.Graphics.UI;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.function.Supplier;

/**
 * Um elemento de UI que renderiza um texto na tela.
 * <p>
 * Esta classe é capaz de exibir tanto texto estático quanto texto dinâmico,
 * cujo conteúdo pode mudar a cada quadro (frame). A dinamicidade é alcançada
 * através de um {@link Supplier<String>}, que fornece o texto atualizado.
 *
 * @author JDStudio
 * @since 1.0
 * @see UIElement
 */
public class UIText extends UIElement {

    /** A fonte usada para renderizar o texto. */
    private Font font;

    /** A cor do texto. */
    private Color color;

    /**
     * Um fornecedor funcional (Supplier) que retorna a string de texto atual.
     * Permite que o texto seja atualizado dinamicamente a cada renderização.
     * Por exemplo, para exibir um contador de FPS: {@code () -> "FPS: " + getFPS()}
     */
    private Supplier<String> textSupplier;

    /**
     * Construtor para criar um elemento de texto dinâmico.
     *
     * @param x            A coordenada horizontal (eixo X) onde o texto será desenhado.
     * @param y            A coordenada vertical (eixo Y) onde o texto será desenhado.
     * @param font         O objeto {@link Font} para estilizar o texto.
     * @param color        O objeto {@link Color} para colorir o texto.
     * @param textSupplier Uma função {@code Supplier} que retorna a string a ser exibida.
     * É executada a cada chamada do método render.
     */
    public UIText(int x, int y, Font font, Color color, Supplier<String> textSupplier) {
        super(x, y);
        this.font = font;
        this.color = color;
        this.textSupplier = textSupplier;
    }

    /**
     * Construtor para criar um elemento de texto estático.
     * <p>
     * Este é um construtor de conveniência que internamente cria um {@code Supplier}
     * para um texto que nunca muda.
     *
     * @param x          A coordenada horizontal (eixo X).
     * @param y          A coordenada vertical (eixo Y).
     * @param font       O objeto {@link Font} para estilizar o texto.
     * @param color      O objeto {@link Color} para colorir o texto.
     * @param staticText A string de texto fixa que será exibida.
     */
    public UIText(int x, int y, Font font, Color color, String staticText) {
        // Reutiliza o construtor principal, envolvendo o texto estático em um Supplier.
        this(x, y, font, color, () -> staticText);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Desenha o texto na tela. A cada chamada, ele obtém o valor mais recente
     * do {@code textSupplier}, define a fonte e a cor no contexto gráfico
     * e, em seguida, desenha a string na posição (x, y) do elemento.
     */
    @Override
    public void render(Graphics g) {
        g.setColor(color);
        g.setFont(font);
        g.drawString(textSupplier.get(), x, y);
    }
}