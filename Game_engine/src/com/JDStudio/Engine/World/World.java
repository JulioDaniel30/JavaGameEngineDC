package com.JDStudio.Engine.World;

/**
 * Gerencia a estrutura do mundo do jogo, composta por uma grade de tiles.
 * <p>
 * Esta classe é responsável por armazenar todos os {@link Tile} do mapa,
 * fornecer acesso a eles e realizar verificações de colisão de entidades
 * contra o cenário (tiles sólidos).
 *
 * @author JDStudio
 * @since 1.0
 */
public class World {

    /** A largura do mundo em unidades de tile. */
    public int WIDTH;

    /** A altura do mundo em unidades de tile. */
    public int HEIGHT;

    /** Um array unidimensional que armazena a grade de tiles bidimensional. */
    protected Tile[] tiles;
    
    /** O tamanho (largura e altura) de cada tile em pixels. */
    public static final int TILE_SIZE = 16;


    /**
     * Cria uma nova instância do mundo com dimensões específicas.
     *
     * @param width  A largura do mundo em número de tiles.
     * @param height A altura do mundo em número de tiles.
     */
    public World(int width, int height) {
        this.WIDTH = width;
        this.HEIGHT = height;
        this.tiles = new Tile[width * height];
    }

    /**
     * Define um tile em uma posição específica do grid.
     *
     * @param x O índice X do tile no grid.
     * @param y O índice Y do tile no grid.
     * @param tile O objeto {@link Tile} a ser colocado.
     */
    public void setTile(int x, int y, Tile tile) {
        if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
            tiles[x + (y * WIDTH)] = tile;
        }
    }

    /**
     * Recupera um tile de uma posição específica do grid.
     *
     * @param x O índice X do tile no grid.
     * @param y O índice Y do tile no grid.
     * @return O {@link Tile} na posição especificada. Se as coordenadas estiverem
     * fora dos limites do mapa, retorna um novo tile temporário e sólido
     * para garantir o comportamento de colisão nas bordas.
     */
    public Tile getTile(int x, int y) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
            // Retorna um tile "fantasma" sólido para fora dos limites.
            return new Tile(x * TILE_SIZE, y * TILE_SIZE, null) {{ isSolid = true; }};
        }
        return tiles[x + (y * WIDTH)];
    }

    /**
     * Verifica se uma área retangular está livre ou se colide com um tile sólido.
     * <p>
     * Este método de colisão verifica os quatro cantos da máscara de colisão da entidade
     * contra a grade de tiles. Assume um tamanho de tile de 16x16.
     *
     * @param x          A coordenada X (em pixels) da posição da entidade.
     * @param y          A coordenada Y (em pixels) da posição da entidade.
     * @param maskX      O deslocamento X da máscara de colisão em relação à posição da entidade.
     * @param maskY      O deslocamento Y da máscara de colisão em relação à posição da entidade.
     * @param maskWidth  A largura da máscara de colisão.
     * @param maskHeight A altura da máscara de colisão.
     * @return {@code true} se a área estiver livre, {@code false} se houver uma colisão com um tile sólido.
     */
    public boolean isFree(int x, int y, int maskX, int maskY, int maskWidth, int maskHeight) {
        // Calcula as coordenadas absolutas da caixa de colisão
        int startX = x + maskX;
        int startY = y + maskY;

        // Calcula as coordenadas dos 4 cantos da máscara em relação à grade de tiles
        int x1 = startX / TILE_SIZE;
        int y1 = startY / TILE_SIZE;

        int x2 = (startX + maskWidth - 1) / TILE_SIZE;
        int y2 = (startY + maskHeight - 1) / TILE_SIZE;

        // Verifica os 4 cantos da máscara de colisão
        if (getTile(x1, y1).isSolid || 
            getTile(x2, y1).isSolid ||
            getTile(x1, y2).isSolid ||
            getTile(x2, y2).isSolid) {
            return false;
        }

        return true;
    }
    
}