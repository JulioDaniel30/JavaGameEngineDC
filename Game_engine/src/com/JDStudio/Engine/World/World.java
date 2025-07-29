// engine
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
 * armazenar todos os {@link Tile} do mapa, e realizar a renderização e
 * verificações de colisão contra o cenário.
 */
public class World {

   

    /** A largura do mundo em unidades de tile. */
    public final int WIDTH;
    /** A altura do mundo em unidades de tile. */
    public final int HEIGHT;

    /** A largura de cada tile em pixels, lida do mapa. */
    public final int tileWidth;
    /** A altura de cada tile em pixels, lida do mapa. */
    public final int tileHeight;

    /** Um array unidimensional que armazena a grade de tiles bidimensional. */
    protected final Tile[] tiles;

    /**
     * Carrega um mundo a partir de um arquivo de mapa JSON (do Tiled), delegando a
     * criação de objetos específicos para um listener.
     * @param mapPath O caminho para o arquivo .json do mapa.
     * @param listener A classe (do jogo) que saberá como construir os tiles e objetos.
     */
    public World(String mapPath, IMapLoaderListener listener) {
        try (InputStream is = getClass().getResourceAsStream(mapPath)) {
            if (is == null) {
                // Lança uma exceção para interromper a criação do mundo se o mapa não for encontrado
                throw new IOException("ERRO CRÍTICO: Não foi possível encontrar o arquivo de mapa: " + mapPath);
            }
            String jsonText = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(jsonText);

            this.WIDTH = json.getInt("width");
            this.HEIGHT = json.getInt("height");
            this.tiles = new Tile[WIDTH * HEIGHT];
            
            // O tamanho do tile agora é lido diretamente do arquivo JSON
            this.tileWidth = json.getInt("tilewidth");
            this.tileHeight = json.getInt("tileheight");

            JSONArray layers = json.getJSONArray("layers");
            for (int i = 0; i < layers.length(); i++) {
                JSONObject layer = layers.getJSONObject(i);
                
                if (layer.getString("type").equals("tilelayer")) {
                    processTileLayer(layer, listener);
                } else if (layer.getString("type").equals("objectgroup")) {
                    processObjectLayer(layer, listener);
                }
            }
        } catch (IOException e) {
            // Lança uma RuntimeException para sinalizar uma falha crítica na inicialização
            throw new RuntimeException("Falha ao criar o mundo a partir de: " + mapPath, e);
        }
    }
    
    private void processTileLayer(JSONObject layer, IMapLoaderListener listener) {
        JSONArray data = layer.getJSONArray("data");
        String layerName = layer.getString("name");

        for (int i = 0; i < data.length(); i++) {
            int tileId = data.getInt(i);
            if (tileId == 0) continue; // Pula tiles vazios

            int x = (i % WIDTH) * this.tileWidth;
            int y = (i / WIDTH) * this.tileHeight;
            
            // Delega a criação do tile, passando o nome da camada e o ID do tile
            Tile createdTile = listener.onTileFound(layerName, tileId, x, y);
            if (createdTile != null) {
                this.tiles[i] = createdTile;
            }
        }
    }

    private void processObjectLayer(JSONObject layer, IMapLoaderListener listener) {
        JSONArray objects = layer.getJSONArray("objects");
        for (int i = 0; i < objects.length(); i++) {
            JSONObject object = objects.getJSONObject(i);
            
            int width = object.getInt("width");
            int height = object.getInt("height");
            int x = object.getInt("x");
            int y = object.getInt("y") - height; 
            String type = object.has("class") ? object.getString("class") : 
                (object.has("type") ? object.getString("type") : "");
  
            // Passamos as novas informações (width, height) para o listener
            listener.onObjectFound(type, x, y, width, height, object);
        }
    }
    
    /**
     * Renderiza a porção visível do mapa na tela.
     */
    public void render(Graphics g) {
        int xstart = Engine.camera.getX() / this.tileWidth;
        int ystart = Engine.camera.getY() / this.tileHeight;
        
        int xfinal = xstart + (Engine.WIDTH / this.tileWidth) + 2;
        int yfinal = ystart + (Engine.HEIGHT / this.tileHeight) + 2;
        
        for (int xx = xstart; xx <= xfinal; xx++) {
            for (int yy = ystart; yy <= yfinal; yy++) {
                Tile tile = getTile(xx, yy);
                if (tile != null) {
                    tile.render(g);
                }
            }
        }
    }
    
    /**
     * Recupera um tile de uma posição específica do grid.
     */
    public Tile getTile(int x, int y) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
            // Retorna um tile "fantasma" sólido para fora dos limites.
            return new Tile(x * this.tileWidth, y * this.tileHeight, this.tileWidth, this.tileHeight, null) {{ isSolid = true; }};
        }
        return tiles[x + (y * WIDTH)];
    }

    /**
     * Verifica se uma área retangular está livre ou se colide com um tile sólido.
     */
    public boolean isFree(int x, int y, int maskX, int maskY, int maskWidth, int maskHeight) {
        int startX = x + maskX;
        int startY = y + maskY;

        int tileX1 = startX / this.tileWidth;
        int tileY1 = startY / this.tileHeight;
        int tileX2 = (startX + maskWidth - 1) / this.tileWidth;
        int tileY2 = (startY + maskHeight - 1) / this.tileHeight;

        for (int iy = tileY1; iy <= tileY2; iy++) {
            for (int ix = tileX1; ix <= tileX2; ix++) {
                Tile tile = getTile(ix, iy);
                if (tile != null && tile.isSolid) {
                    return false; // Área bloqueada
                }
            }
        }
        return true;
    }
}