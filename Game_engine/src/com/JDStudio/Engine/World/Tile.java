package com.JDStudio.Engine.World;

import java.awt.Color;
import java.awt.Graphics;

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Graphics.Layers.IRenderable;
import com.JDStudio.Engine.Graphics.Layers.RenderLayer;
import com.JDStudio.Engine.Graphics.Layers.StandardLayers;
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
public class Tile implements IRenderable{

	  protected final Sprite sprite;
	    protected final int x, y, width, heigth;
	    public boolean isSolid = false;

	    // --- NOVA VARIÁVEL ---
	    // Por padrão, todos os tiles são desenhados na camada de fundo.
	    protected RenderLayer renderLayer = StandardLayers.WORLD_BACKGROUND;

    /**
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
    @Override
    public void render(Graphics g) {
        if (sprite != null) {
        	g.drawImage(sprite.getImage(), x - Engine.camera.getX(), y - Engine.camera.getY(), null);
        }
        if (Engine.isDebug && this.isSolid) {
            g.setColor(Color.BLUE);
            g.drawRect(x - Engine.camera.getX(), y - Engine.camera.getY(), width, heigth);
        }
    }

    @Override
    public RenderLayer getRenderLayer() {
        return this.renderLayer;
    }

    @Override
    public int getZOrder() {
        // Para tiles, a posição Y é um bom Z-order para garantir que
        // os de primeiro plano mais abaixo sejam desenhados por cima.
        return this.y + this.heigth;
    }

    @Override
    public boolean isVisible() {
        // Poderíamos adicionar aqui uma lógica para não renderizar tiles fora da tela,
        // mas por enquanto, vamos mantê-lo simples.
        return true;
    }
    
 // --- NOVO MÉTODO PARA ALTERAR A CAMADA ---
    public void setRenderLayer(RenderLayer layer) {
        this.renderLayer = layer;
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