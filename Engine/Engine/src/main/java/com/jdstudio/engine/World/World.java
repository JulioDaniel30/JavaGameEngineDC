package com.jdstudio.engine.World;

import java.awt.Graphics;
import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Object.GameObject;
import com.jdstudio.engine.World.Tile.TileType;

/**
 * Represents the game world, loaded from a Tiled JSON map file.
 * <p>
 * This class manages the grid of tiles, handles map loading, and provides
 * core functionalities like rendering the visible portion of the map and
 * performing collision checks. It uses a listener-based approach to delegate
 * the creation of tiles and game objects, making it highly extensible.
 */
public class World {

    /** The width of the world in tiles. */
    public final int WIDTH;
    /** The height of the world in tiles. */
    public final int HEIGHT;
    /** The width of a single tile in pixels. */
    public final int tileWidth;
    /** The height of a single tile in pixels. */
    public final int tileHeight;
    /** The array holding all the tiles in the world, stored in a 1D array. */
    protected final Tile[] tiles;

    /**
     * Constructs a new World by loading and parsing a map file from the specified path.
     *
     * @param mapPath  The resource path to the Tiled JSON map file.
     * @param listener An {@link IMapLoaderListener} that will handle the creation of
     *                 tiles, objects, and paths found in the map file.
     * @throws RuntimeException if the map file cannot be found or read.
     */
    public World(String mapPath, IMapLoaderListener listener) {
        try (InputStream is = getClass().getResourceAsStream(mapPath)) {
            if (is == null) {
                throw new IOException("CRITICAL ERROR: Map file not found: " + mapPath);
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

            // First pass: Separate layers by type to process them in a specific order.
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

            // Second pass: Process the data in the correct order (tiles, then paths, then objects).
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
            throw new RuntimeException("Failed to create world from: " + mapPath, e);
        }
    }
    
    /**
     * Processes a Tiled "polyline" object, converting it into a path for the listener.
     * @param object The JSON object representing the polyline.
     * @param listener The listener to notify when the path is found.
     */
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
    
    /**
     * Processes a regular Tiled "object", notifying the listener to create a game object.
     * @param object The JSON object to process.
     * @param listener The listener to notify.
     */
    private void processRegularObject(JSONObject object, IMapLoaderListener listener) {
        int x = object.getInt("x");
        int width = object.getInt("width");
        int height = object.getInt("height");
        int y = object.getInt("y") - height; // Tiled's y-origin is top-left, adjust for bottom-left
        String type = object.has("class") ? object.getString("class") : 
                      (object.has("type") ? object.getString("type") : "");
        listener.onObjectFound(type, x, y, width, height, object);
    }

    /**
     * Processes a "tilelayer" from the Tiled map, creating tiles via the listener.
     * @param layer The JSON layer object.
     * @param listener The listener to notify for each tile found.
     */
    private void processTileLayer(JSONObject layer, IMapLoaderListener listener) {
        JSONArray data = layer.getJSONArray("data");
        String layerName = layer.getString("name");
        for (int i = 0; i < data.length(); i++) {
            int tileId = data.getInt(i);
            if (tileId == 0) continue; // 0 is an empty tile
            int x = (i % WIDTH) * this.tileWidth;
            int y = (i / WIDTH) * this.tileHeight;
            Tile createdTile = listener.onTileFound(layerName, tileId, x, y);
            if (createdTile != null) {
                this.tiles[i] = createdTile;
            }
        }
    }
    
    /**
     * Renders the visible portion of the world.
     * It calculates the camera's view and only renders the tiles within that boundary,
     * which is highly efficient for large maps.
     *
     * @param g The Graphics context to draw on.
     */
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
    
    /**
     * Sets or replaces a tile at a specific grid location.
     *
     * @param x    The x-coordinate in the tile grid.
     * @param y    The y-coordinate in the tile grid.
     * @param tile The new Tile to place at the location.
     */
    public void setTile(int x, int y, Tile tile) {
        if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
            tiles[x + y * WIDTH] = tile;
        }
    }

    /**
     * Retrieves a tile from a specific grid location.
     *
     * @param x The x-coordinate in the tile grid.
     * @param y The y-coordinate in the tile grid.
     * @return The {@link Tile} at the location. If the coordinates are out of bounds,
     *         a non-solid, empty tile is returned to prevent null pointer exceptions.
     */
    public Tile getTile(int x, int y) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
            // Return a default, non-solid tile for out-of-bounds requests.
            return new Tile(x * this.tileWidth, y * this.tileHeight, this.tileWidth, this.tileHeight, null);
        }
        return tiles[x + (y * WIDTH)];
    }

    /**
     * Checks if a rectangular area, defined by a collision mask, is free of solid tiles.
     *
     * @param x          The world x-coordinate of the object.
     * @param y          The world y-coordinate of the object.
     * @param maskX      The x-offset of the collision mask relative to the object's position.
     * @param maskY      The y-offset of the collision mask relative to the object's position.
     * @param maskWidth  The width of the collision mask.
     * @param maskHeight The height of the collision mask.
     * @return {@code true} if the area is free of solid tiles, {@code false} otherwise.
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
                if (tile != null && tile.getTileType() == TileType.SOLID) return false;
            }
        }
        return true;
    }
    
    /**
     * Checks if the area occupied by a GameObject's collision mask is free.
     * This version includes special handling for one-way (platform) tiles.
     *
     * @param movingObject The {@link GameObject} to check for collision.
     * @return {@code true} if the area is free of solid tiles, {@code false} otherwise.
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
                        return false; // Solid wall collision
                    }
                    if (tile.tileType == Tile.TileType.ONE_WAY) {
                        // ONE-WAY PLATFORM LOGIC
                        // Check if the object's bottom is above the platform's top
                        // and if the object is moving downwards.
                        if ((startY + maskHeight) <= tile.getY() + 4 && movingObject.velocityY >= 0) {
                            
                            // Snap the object's Y position to the top of the tile and stop vertical movement.
                            movingObject.setY(tile.getY() - maskHeight);
                            movingObject.velocityY = 0;
                            movingObject.onGround = true; // Notify physics component it is on the ground.
                        }
                    }
                }
            }
        }
        return true;
    }
}