package com.JDStudio.Engine.Graphics.UI.Elements;

import java.awt.Graphics;
import java.util.function.Supplier;

import com.JDStudio.Engine.Graphics.Sprite.Sprite;

/**
 * Um elemento de UI que exibe uma imagem cuja fonte é dinâmica.
 * A cada frame, ele consulta um "fornecedor" (Supplier) para obter o Sprite
 * correto a ser desenhado, permitindo que a imagem mude em tempo real
 * com base no estado do jogo.
 */
public class UIDynamicImage extends UIElement {

    private Supplier<Sprite> spriteSupplier;
    private Sprite currentSprite;

    /**
     * Cria um novo elemento de UI de imagem dinâmica.
     * @param x Posição X na tela.
     * @param y Posição Y na tela.
     * @param spriteSupplier Uma função que retorna o Sprite a ser exibido a cada frame.
     */
    public UIDynamicImage(int x, int y, Supplier<Sprite> spriteSupplier) {
        super(x, y);
        this.spriteSupplier = spriteSupplier;
    }

    @Override
    public void tick() {
        if (!visible || spriteSupplier == null) return;
        
        // A cada tick, busca o sprite mais recente do fornecedor.
        this.currentSprite = spriteSupplier.get();
        
        // Atualiza as dimensões do elemento com base no sprite atual
        if (this.currentSprite != null) {
            this.width = currentSprite.getWidth();
            this.height = currentSprite.getHeight();
        }
    }

    @Override
    public void render(Graphics g) {
        if (visible && currentSprite != null) {
            g.drawImage(currentSprite.getImage(), x, y, null);
        }
    }
}