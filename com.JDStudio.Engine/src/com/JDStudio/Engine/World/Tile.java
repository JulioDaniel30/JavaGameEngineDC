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

	
	// --- NOVO ENUM PARA OS TIPOS DE TILE ---
    public enum TileType {
        PASSABLE,      // O tile não tem colisão (ex: chão de fundo).
        SOLID,         // O tile é uma parede sólida.
        ONE_WAY        // O tile é uma plataforma "pula-através".
    }
	
	  protected final Sprite sprite;
	  protected final int x, y, width, height; 
	  public TileType tileType = TileType.PASSABLE;
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
	        // Assume o tamanho padrão do sprite se disponível
	        if (sprite != null) {
	            this.width = sprite.getWidth();
	            this.height = sprite.getHeight();
	        } else {
	            this.width = 16;
	            this.height = 16;
	        }
	    }

	    public Tile(int x, int y, int width, int height, Sprite sprite) {
	        this.x = x;
	        this.y = y;
	        this.sprite = sprite;
	        this.width = width;
	        this.height = height;
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
        if (Engine.isDebug && this.tileType != TileType.PASSABLE) {
            if (this.tileType == TileType.SOLID) g.setColor(Color.BLUE);
            else if (this.tileType == TileType.ONE_WAY) g.setColor(Color.GREEN);
            g.drawRect(x - Engine.camera.getX(), y - Engine.camera.getY(), width, height);
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
        return this.y + this.height;
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
    public TileType getTileType() {
        return tileType;
    }
    //</editor-fold>
}