// engine
package com.JDStudio.Engine.World;

import java.awt.Graphics;
import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Object.GameObject;
import com.JDStudio.Engine.World.Tile.TileType;

public class World {

    public final int WIDTH;
    public final int HEIGHT;
    public final int tileWidth;
    public final int tileHeight;
    protected final Tile[] tiles;

    public World(String mapPath, IMapLoaderListener listener) {
        try (InputStream is = getClass().getResourceAsStream(mapPath)) {
            if (is == null) {
                throw new IOException("ERRO CRÍTICO: Não foi possível encontrar o arquivo de mapa: " + mapPath);
            }
            String jsonText = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(jsonText);

            this.WIDTH = json.getInt("width");
            this.HEIGHT = json.getInt("height");
            this.tiles = new Tile[WIDTH * HEIGHT];
            this.tileWidth = json.getInt("tilewidth");
            this.tileHeight = json.getInt("tileheight");

            JSONArray layers = json.getJSONArray("layers");
            
            List<JSONObject> tileLayers = new ArrayList<>();
            List<JSONObject> pathObjects = new ArrayList<>();
            List<JSONObject> regularObjects = new ArrayList<>();

            // 1. PRIMEIRA PASSAGEM: Separa os dados por tipo
            for (int i = 0; i < layers.length(); i++) {
                JSONObject layer = layers.getJSONObject(i);
                if (layer.getString("type").equals("tilelayer")) {
                    tileLayers.add(layer);
                } else if (layer.getString("type").equals("objectgroup")) {
                    JSONArray objects = layer.getJSONArray("objects");
                    for (int j = 0; j < objects.length(); j++) {
                        JSONObject object = objects.getJSONObject(j);
                        if (object.has("polyline")) {
                            pathObjects.add(object);
                        } else {
                            regularObjects.add(object);
                        }
                    }
                }
            }

            // 2. SEGUNDA PASSAGEM: Processa os dados na ordem correta
            for (JSONObject layer : tileLayers) {
                processTileLayer(layer, listener);
            }
            for (JSONObject pathObject : pathObjects) {
                processPathObject(pathObject, listener);
            }
            for (JSONObject regularObject : regularObjects) {
                processRegularObject(regularObject, listener);
            }

        } catch (IOException e) {
            throw new RuntimeException("Falha ao criar o mundo a partir de: " + mapPath, e);
        }
    }
    
    private void processPathObject(JSONObject object, IMapLoaderListener listener) {
        String pathName = object.getString("name");
        List<Point> pathPoints = new ArrayList<>();
        JSONArray polylinePoints = object.getJSONArray("polyline");
        int startX = object.getInt("x");
        int startY = object.getInt("y");
        for (int j = 0; j < polylinePoints.length(); j++) {
            JSONObject pointJson = polylinePoints.getJSONObject(j);
            pathPoints.add(new Point(startX + pointJson.getInt("x"), startY + pointJson.getInt("y")));
        }
        listener.onPathFound(pathName, pathPoints);
    }
    
    private void processRegularObject(JSONObject object, IMapLoaderListener listener) {
        int x = object.getInt("x");
        int width = object.getInt("width");
        int height = object.getInt("height");
        int y = object.getInt("y") - height;
        String type = object.has("class") ? object.getString("class") : 
                      (object.has("type") ? object.getString("type") : "");
        listener.onObjectFound(type, x, y, width, height, object);
    }

    private void processTileLayer(JSONObject layer, IMapLoaderListener listener) {
        JSONArray data = layer.getJSONArray("data");
        String layerName = layer.getString("name");
        for (int i = 0; i < data.length(); i++) {
            int tileId = data.getInt(i);
            if (tileId == 0) continue;
            int x = (i % WIDTH) * this.tileWidth;
            int y = (i / WIDTH) * this.tileHeight;
            Tile createdTile = listener.onTileFound(layerName, tileId, x, y);
            if (createdTile != null) {
                this.tiles[i] = createdTile;
            }
        }
    }
    
    public void render(Graphics g) {
        int xstart = Engine.camera.getX() / this.tileWidth;
        int ystart = Engine.camera.getY() / this.tileHeight;
        int xfinal = xstart + (Engine.getWIDTH() / this.tileWidth) + 2;
        int yfinal = ystart + (Engine.getHEIGHT() / this.tileHeight) + 2;
        
        for (int xx = xstart; xx <= xfinal; xx++) {
            for (int yy = ystart; yy <= yfinal; yy++) {
                Tile tile = getTile(xx, yy);
                if (tile != null) tile.render(g);
            }
        }
    }
    
    public void setTile(int x, int y, Tile tile) {
        if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
            tiles[x + y * WIDTH] = tile;
        }
    }

    public Tile getTile(int x, int y) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
            return new Tile(x * this.tileWidth, y * this.tileHeight, this.tileWidth, this.tileHeight, null);
        }
        return tiles[x + (y * WIDTH)];
    }

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
                if (tile != null && tile.getTileType() == TileType.SOLID) return false;
            }
        }
        return true;
    }
    /**
     * Verifica se uma área retangular no mundo está livre de tiles sólidos.
     * Agora inclui a lógica para plataformas "pula-através".
     * @param movingObject O GameObject que está a tentar mover-se.
     * @return true se a área estiver livre, false caso contrário.
     */
    public boolean isFree(GameObject movingObject) {
        int startX = movingObject.getX() + movingObject.getMaskX();
        int startY = movingObject.getY() + movingObject.getMaskY();
        int maskWidth = movingObject.getMaskWidth();
        int maskHeight = movingObject.getMaskHeight();

        int tileX1 = startX / this.tileWidth;
        int tileY1 = startY / this.tileHeight;
        int tileX2 = (startX + maskWidth - 1) / this.tileWidth;
        int tileY2 = (startY + maskHeight - 1) / this.tileHeight;

        for (int iy = tileY1; iy <= tileY2; iy++) {
            for (int ix = tileX1; ix <= tileX2; ix++) {
                Tile tile = getTile(ix, iy);
                if (tile != null) {
                    if (tile.tileType == Tile.TileType.SOLID) {
                        return false; // Colisão com parede sólida
                    }
                    if (tile.tileType == Tile.TileType.ONE_WAY) {
                        // LÓGICA DA PLATAFORMA "PULA-ATRAVÉS"
                        // Verifica se o pé do personagem está acima do topo do tile
                        // e se o personagem está a mover-se para baixo.
                        if ((startY + maskHeight) <= tile.getY() + 4 && movingObject.velocityY >= 0) {
                            
                            // Ajusta a posição Y do jogador para o topo do tile e para a sua velocidade
                            movingObject.setY(tile.getY() - maskHeight);
                            movingObject.velocityY = 0;
                            movingObject.onGround = true; // Informa ao componente de física que está no chão

                            // Continua a verificar outros tiles, mas a colisão vertical foi resolvida.
                        }
                    }
                }
            }
        }
        return true;
    }
}