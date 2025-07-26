package com.JDStudio.Engine.World;

import java.awt.Color;
import java.awt.Graphics;

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Graphics.Sprite.Sprite;

/**
 * Representa um único tile estático no mapa do jogo.
 * <p>
 * Tiles são os blocos de construção do mundo do jogo, definindo a aparência
 * e as propriedades físicas de cada célula do grid, como se é sólida ou não.
 *
 * @author JDStudio
 * @since 1.0
 */
public class Tile {

    /** O sprite (imagem) que representa visualmente este tile. */
    protected final Sprite sprite;

    /** A coordenada X (em pixels) do canto superior esquerdo do tile no mundo. */
    protected final int x;

    /** A coordenada Y (em pixels) do canto superior esquerdo do tile no mundo. */
    protected final int y;
    protected final int width;
    protected final int heigth;

    /** Define se o tile é sólido. Se {@code true}, GameObjects não podem atravessá-lo. */
    public boolean isSolid = false;

    /**
     * Construtor para um novo Tile.
     *
     * @param x      A posição X inicial (em pixels) no mundo.
     * @param y      A posição Y inicial (em pixels) no mundo.
     * @param sprite O {@link Sprite} a ser usado para renderizar este tile.
     */
    public Tile(int x, int y, Sprite sprite) {
        this.x = x;
        this.y = y;
        this.sprite = sprite;
        this.heigth = 16;
        this.width = 16;
    }
    public Tile(int x, int y, int width, int heigth, Sprite sprite) {
        this.x = x;
        this.y = y;
        this.sprite = sprite;
        this.heigth = heigth;
        this.width = width;
    }

    /**
     * Renderiza o tile na tela, ajustado pela posição da câmera.
     * <p>
     * Se o modo de debug ({@code Engine.isDebug}) estiver ativo e o tile for sólido,
     * um contorno azul será desenhado sobre ele para visualização da hitbox.
     *
     * @param g O contexto {@link Graphics} onde o tile será desenhado.
     */
    public void render(Graphics g) {
        // Desenha o sprite do tile
        g.drawImage(sprite.getImage(), x - Camera.x, y - Camera.y, null);
        
        // Desenha a hitbox de debug se necessário
        if (Engine.isDebug && this.isSolid) {
            g.setColor(Color.BLUE);
            g.drawRect(x - Camera.x, y - Camera.y, 16, 16); // Assumindo tiles de 16x16
        }
    }
    
    //<editor-fold desc="Getters">
    /** Retorna a coordenada X do tile no mundo. */
    public int getX() {
        return x;
    }

    /** Retorna a coordenada Y do tile no mundo. */
    public int getY() {
        return y;
    }

    /** Verifica se o tile é sólido. */
    public boolean isSolid() {
        return isSolid;
    }
    //</editor-fold>
}