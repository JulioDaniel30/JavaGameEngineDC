package com.JDStudio.Engine.World;

import java.awt.Graphics;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;

import com.JDStudio.Engine.Engine;

/**
 * Gerencia a estrutura do mundo do jogo, composta por uma grade de tiles.
 * <p>
 * Esta classe é responsável por carregar um mapa a partir de um arquivo JSON do Tiled,
 * armazenar todos os {@link Tile} do mapa, fornecer acesso a eles e realizar
 * verificações de colisão de entidades contra o cenário (tiles sólidos).
 *
 * @author JDStudio
 * @since 1.1
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
     * Carrega um mundo a partir de um arquivo de mapa JSON (do Tiled), delegando a
     * criação de objetos específicos para um listener.
     * @param mapPath O caminho para o arquivo .json do mapa.
     * @param listener A classe (do jogo) que saberá como construir os tiles e objetos.
     */
    public World(String mapPath, IMapLoaderListener listener) {
        // --- O CÓDIGO RESTAURADO ESTÁ AQUI ---
        try {
            InputStream is = getClass().getResourceAsStream(mapPath);
            if (is == null) {
                System.err.println("ERRO CRÍTICO: Não foi possível encontrar o arquivo de mapa: " + mapPath);
                return;
            }
            String jsonText = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(jsonText);

            this.WIDTH = json.getInt("width");
            this.HEIGHT = json.getInt("height");
            this.tiles = new Tile[WIDTH * HEIGHT];
            
            int tileWidth = json.getInt("tilewidth");
            int tileHeight = json.getInt("tileheight");

            JSONArray layers = json.getJSONArray("layers");
            for (int i = 0; i < layers.length(); i++) {
                JSONObject layer = layers.getJSONObject(i);
                
                if (layer.getString("type").equals("tilelayer")) {
                    processTileLayer(layer, tileWidth, tileHeight, listener);
                } else if (layer.getString("type").equals("objectgroup")) {
                    processObjectLayer(layer, tileWidth, tileHeight, listener);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // --- FIM DO CÓDIGO RESTAURADO ---
    }
    
    private void processTileLayer(JSONObject layer, int tileWidth, int tileHeight, IMapLoaderListener listener) {
        JSONArray data = layer.getJSONArray("data");
        for (int i = 0; i < data.length(); i++) {
            int tileId = data.getInt(i);
            if (tileId == 0) continue; // Pula tiles vazios

            int x = (i % WIDTH) * tileWidth;
            int y = (i / WIDTH) * tileHeight;
            
            // A engine DELEGA a criação do tile para o listener do jogo
            Tile createdTile = listener.onTileFound(tileId, x, y);
            if (createdTile != null) {
                this.tiles[i] = createdTile;
            }
        }
    }

    private void processObjectLayer(JSONObject layer, int tileWidth, int tileHeight, IMapLoaderListener listener) {
        JSONArray objects = layer.getJSONArray("objects");
        for (int i = 0; i < objects.length(); i++) {
            JSONObject object = objects.getJSONObject(i);
            int x = object.getInt("x");
            // Tiled posiciona objetos pela base, ajustamos para o topo
            int y = object.getInt("y") - tileHeight; 
            String type = object.has("type") ? object.getString("type") : "";
            
            // A engine DELEGA a criação do objeto para o listener do jogo
            listener.onObjectFound(type, x, y, object);
        }
    }
    
    /**
     * Renderiza a porção visível do mapa na tela.
     */
    public void render(Graphics g) {
        int xstart = Camera.x / TILE_SIZE;
        int ystart = Camera.y / TILE_SIZE;
        
        int xfinal = xstart + (Engine.WIDTH / TILE_SIZE) + 1;
        int yfinal = ystart + (Engine.HEIGHT / TILE_SIZE) + 1;
        
        for (int xx = xstart; xx <= xfinal; xx++) {
            for (int yy = ystart; yy <= yfinal; yy++) {
                Tile tile = getTile(xx, yy);
                if (tile != null) {
                    tile.render(g);
                }
            }
        }
    }

    // ... (os seus métodos getTile, setTile e isFree estão corretos e não precisam de alteração)
    
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