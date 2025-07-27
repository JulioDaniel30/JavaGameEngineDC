// engine
package com.JDStudio.Engine.World;

import java.awt.Graphics;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;

import com.JDStudio.Engine.Engine;

public class World {

    public int WIDTH;  // Largura do mundo em tiles
    public int HEIGHT; // Altura do mundo em tiles
    
    // --- MUDANÇA PRINCIPAL ---
    // O tamanho do tile agora é específico para cada instância do mundo.
    public int tileWidth;
    public int tileHeight;

    protected Tile[] tiles;
    
    // A constante estática foi removida daqui.
    // public static final int TILE_SIZE = 16;

    public World(String mapPath, IMapLoaderListener listener) {
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
            
            // --- ATRIBUIÇÃO DOS NOVOS ATRIBUTOS ---
            // Lemos o tamanho do tile do próprio arquivo JSON.
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
            e.printStackTrace();
        }
    }
    
    private void processTileLayer(JSONObject layer, IMapLoaderListener listener) {
        JSONArray data = layer.getJSONArray("data");
        for (int i = 0; i < data.length(); i++) {
            int tileId = data.getInt(i);
            if (tileId == 0) continue;

            // Usa os novos atributos para calcular a posição em pixels
            int x = (i % WIDTH) * this.tileWidth;
            int y = (i / WIDTH) * this.tileHeight;
            
            Tile createdTile = listener.onTileFound(tileId, x, y);
            if (createdTile != null) {
                this.tiles[i] = createdTile;
            }
        }
    }

    private void processObjectLayer(JSONObject layer, IMapLoaderListener listener) {
        JSONArray objects = layer.getJSONArray("objects");
        for (int i = 0; i < objects.length(); i++) {
            JSONObject object = objects.getJSONObject(i);
            int x = object.getInt("x");
            // Ajuste usa o tileHeight do mapa
            int y = object.getInt("y") - this.tileHeight; 
            String type = object.has("type") ? object.getString("type") : "";
            
            listener.onObjectFound(type, x, y, object);
        }
    }
    
    public void render(Graphics g) {
        // Usa os novos atributos para calcular a área de renderização
        int xstart = Engine.camera.getX() / this.tileWidth;
        int ystart = Engine.camera.getY() / this.tileHeight;
        
        int xfinal = xstart + (Engine.WIDTH / this.tileWidth) + 2; // Adicionado +2 para garantir cobertura com zoom/scroll
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

    public void setTile(int x, int y, Tile tile) {
        if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
            tiles[x + (y * WIDTH)] = tile;
        }
    }

    public Tile getTile(int x, int y) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
            // Usa os novos atributos para criar o tile "fantasma"
            return new Tile(x * this.tileWidth, y * this.tileHeight, this.tileWidth, this.tileHeight, null) {{ isSolid = true; }};
        }
        return tiles[x + (y * WIDTH)];
    }

    public boolean isFree(int x, int y, int maskX, int maskY, int maskWidth, int maskHeight) {
        int startX = x + maskX;
        int startY = y + maskY;

        // Usa os novos atributos para converter de pixels para a grade de tiles
        int tileX1 = startX / this.tileWidth;
        int tileY1 = startY / this.tileHeight;
        int tileX2 = (startX + maskWidth - 1) / this.tileWidth;
        int tileY2 = (startY + maskHeight - 1) / this.tileHeight;

        for (int iy = tileY1; iy <= tileY2; iy++) {
            for (int ix = tileX1; ix <= tileX2; ix++) {
                Tile tile = getTile(ix, iy);
                if (tile != null && tile.isSolid) {
                    return false;
                }
            }
        }
        return true;
    }

}


