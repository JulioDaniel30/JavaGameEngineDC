package com.JDStudio.Engine.Graphics.UI.Elements;

import java.awt.Graphics;
import com.JDStudio.Engine.Graphics.Sprite.Sprite;

/**
 * Um elemento de UI que simplesmente renderiza um Sprite na tela.
 * Ideal para ícones, imagens de fundo de menus, avatares, etc.
 */
public class UIImage extends UIElement {

    private Sprite sprite;

    /**
     * Cria um novo elemento de UI de imagem.
     * @param x Posição X na tela.
     * @param y Posição Y na tela.
     * @param sprite O Sprite a ser desenhado.
     */
    public UIImage(int x, int y, Sprite sprite) {
        super(x, y);
        setSprite(sprite); // Usa o setter para definir o sprite e as dimensões
    }

    // tick() é herdado de UIElement e pode ser usado no futuro para animações (ex: piscar).
    // Por enquanto, não precisa de lógica.

    @Override
    public void render(Graphics g) {
        if (visible && sprite != null) {
            // Desenha o sprite na posição x, y da tela (sem offset de câmera)
            g.drawImage(sprite.getImage(), x, y, null);
        }
    }

    /**
     * Permite alterar a imagem do elemento dinamicamente durante o jogo.
     * @param newSprite O novo Sprite a ser exibido.
     */
    public void setSprite(Sprite newSprite) {
        this.sprite = newSprite;
        if (this.sprite != null) {
            this.width = this.sprite.getWidth();
            this.setHeight(this.sprite.getHeight());
        }
    }
}