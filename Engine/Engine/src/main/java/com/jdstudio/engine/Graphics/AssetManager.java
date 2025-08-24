package com.jdstudio.engine.Graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jdstudio.engine.Graphics.Sprite.Sprite;
import com.jdstudio.engine.Graphics.Sprite.Spritesheet;

/**
 * Manages the loading, caching, and access to all game sprites.
 * <p>
 * This class centralizes resource management to prevent duplicate image loading,
 * saving memory and improving performance. It ensures that each resource is
 * identified by a unique text key.
 *
 * @author JDStudio
 * @since 1.0
 */
public class AssetManager {

    /** Cache to store loaded sprites, associating a unique key with each {@link Sprite}. */
    private Map<String, Sprite> spriteCache;

    /**
     * Constructs a new AssetManager.
     * Initializes the internal sprite cache.
     */
    public AssetManager() {
        this.spriteCache = new HashMap<>();
    }
    
    /**
     * Loads multiple sprites from a JSON configuration file.
     * The JSON should contain a "sprites" array with objects like { "key": "player", "path": "/sprites/player.png" }.
     *
     * @param jsonPath Path to the JSON file in the classpath (e.g., "/configs/sprites.json")
     */
    public void loadSpritesFromJson(String jsonPath) {
        try (InputStream is = getClass().getResourceAsStream(jsonPath)) {
            if (is == null) {
                System.err.println("Failed to find sprite configuration file: " + jsonPath);
                return;
            }
            String jsonText = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject config = new JSONObject(jsonText);
            JSONArray spritesArray = config.getJSONArray("sprites");
            for (int i = 0; i < spritesArray.length(); i++) {
                JSONObject spriteData = spritesArray.getJSONObject(i);
                String key = spriteData.getString("key");
                String path = spriteData.getString("path");
                loadSprite(key, path);
            }
            System.out.println(spritesArray.length() + " sprites loaded successfully from: " + jsonPath);
        } catch (Exception e) {
            System.err.println("Error processing sprite configuration file '" + jsonPath + "'.");
            e.printStackTrace();
        }
    }
    
    /**
     * Loads and crops multiple sprites from a single spritesheet, based on
     * definitions from a JSON configuration file.
     * Supports definitions based on grid, full grid, and manual coordinates.
     * <p>
     * JSON Structure Examples:
     * <pre>
     * {
     *   "spritesheetPath": "/sprites/my_sheet.png",
     *   "definitions": [
     *     { "type": "grid", "prefix": "player_walk_", "spriteWidth": 32, "spriteHeight": 32, "startX": 0, "startY": 0, "numCols": 4, "numRows": 2, "startIndex": 1 },
     *     { "type": "full_grid", "prefix": "tile_", "spriteWidth": 16, "spriteHeight": 16, "startIndex": 0 },
     *     { "type": "manual", "sprites": [
     *       { "key": "player_idle", "x": 0, "y": 0, "w": 32, "h": 32 },
     *       { "key": "enemy_attack", "x": 32, "y": 0, "w": 64, "h": 64 }
     *     ]}
     *   ]
     * }
     * </pre>
     *
     * @param jsonPath The path to the .json resource file (e.g., "/configs/player_sprites.json").
     */
    public void loadSpritesFromSpritesheetJson(String jsonPath) {
        try (InputStream is = getClass().getResourceAsStream(jsonPath)) {
            if (is == null) {
                System.err.println("Failed to find spritesheet definition file: " + jsonPath);
                return;
            }

            String jsonText = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject config = new JSONObject(jsonText);

            // 1. Load the main spritesheet
            String sheetPath = config.getString("spritesheetPath");
            Spritesheet sheet = new Spritesheet(sheetPath);

            JSONArray definitions = config.getJSONArray("definitions");
            int spritesRegistered = 0;

            // 2. Iterate over each definition block (grid, full_grid, or manual)
            for (int i = 0; i < definitions.length(); i++) {
                JSONObject def = definitions.getJSONObject(i);
                String type = def.getString("type");

                if ("grid".equals(type)) {
                	 String prefix = def.getString("prefix");
                     int spriteWidth = def.getInt("spriteWidth");
                     int spriteHeight = def.getInt("spriteHeight");
                     int startX = def.optInt("startX", 0);
                     int startY = def.optInt("startY", 0);

                     int startIndex = def.optInt("startIndex", 1);

                     if (def.has("numCols") && def.has("numRows")) {
                         int numCols = def.getInt("numCols");
                         int numRows = def.getInt("numRows");
                         
                         int counter = startIndex;
                         for (int row = 0; row < numRows; row++) {
                             for (int col = 0; col < numCols; col++) {
                                 String key = prefix + counter;
                                 int x = startX + (col * spriteWidth);
                                 int y = startY + (row * spriteHeight);
                                 registerSprite(key, sheet.getSprite(x, y, spriteWidth, spriteHeight));
                                 spritesRegistered++;
                                 counter++;
                             }
                         }
                     } 
                     else if (def.has("count")) {
                         int count = def.getInt("count");
                         for (int j = 0; j < count; j++) {
                             String key = prefix + (startIndex + j);
                             int x = startX + (j * spriteWidth);
                             registerSprite(key, sheet.getSprite(x, startY, spriteWidth, spriteHeight));
                             spritesRegistered++;
                         }
                     }
                } else if ("full_grid".equals(type)) {
                    String prefix = def.getString("prefix");
                    int spriteWidth = def.getInt("spriteWidth");
                    int spriteHeight = def.getInt("spriteHeight");
                    int startIndex = def.optInt("startIndex", 1);
                    
                    int numCols = sheet.getWidth() / spriteWidth;
                    int numRows = sheet.getHeight() / spriteHeight;
                    
                    int counter = startIndex;

                    for (int row = 0; row < numRows; row++) {
                        for (int col = 0; col < numCols; col++) {
                            String key = prefix + counter;
                            int x = col * spriteWidth;
                            int y = row * spriteHeight;
                            
                            registerSprite(key, sheet.getSprite(x, y, spriteWidth, spriteHeight));
                            spritesRegistered++;
                            counter++;
                        }
                    }

                } else if ("manual".equals(type)) {
                    JSONArray manualSprites = def.getJSONArray("sprites");
                    for (int j = 0; j < manualSprites.length(); j++) {
                        JSONObject spriteData = manualSprites.getJSONObject(j);
                        String key = spriteData.getString("key");
                        int x = spriteData.getInt("x");
                        int y = spriteData.getInt("y");
                        int w = spriteData.getInt("w");
                        int h = spriteData.getInt("h");
                        registerSprite(key, sheet.getSprite(x, y, w, h));
                        spritesRegistered++;
                    }
                }
            }
            System.out.println(spritesRegistered + " sprites cropped and registered successfully from: " + sheetPath);

        } catch (Exception e) {
            System.err.println("Error processing spritesheet definition file '" + jsonPath + "'. Check JSON format.");
            e.printStackTrace();
        }
    }

    /**
     * Loads a sprite from an image file and stores it in the cache.
     * <p>
     * If a sprite with the same key already exists in the cache, the operation is ignored.
     * In case of loading failure, an error message is displayed in the console,
     * but the application is not interrupted.
     *
     * @param key  The unique name (key) for this sprite (e.g., "player", "bullet").
     * @param path The path to the image resource, accessible via the Classpath (e.g., "/sprites/player.png").
     */
    public void loadSprite(String key, String path) {
        if (spriteCache.containsKey(key)) {
            return; // Optimization: do not load if already exists.
        }
        try {
            BufferedImage image = ImageIO.read(getClass().getResource(path));
            spriteCache.put(key, new Sprite(image));
        } catch (IOException | IllegalArgumentException | NullPointerException e) {
            System.err.println("Failed to load sprite '" + key + "' from path: " + path);
            e.printStackTrace(); // Useful for detailed debugging
        }
    }

    /**
     * Registers an existing {@link Sprite} object in the cache.
     * <p>
     * This method is ideal for adding sprites that were created from a {@code Spritesheet},
     * where a single image is sliced into multiple sprites.
     *
     * @param key    The unique name (key) for this sprite.
     * @param sprite The {@code Sprite} object to be added to the cache.
     */
    public void registerSprite(String key, Sprite sprite) {
        // Null check adds robustness
        if (sprite == null) {
            System.err.println("Attempt to register a null sprite with key: '" + key + "'");
            return;
        }
        if (spriteCache.containsKey(key)) {
            System.err.println("Warning: Key '" + key + "' already exists in cache. Sprite will not be replaced.");
            return;
        }
        spriteCache.put(key, sprite);
    }

    /**
     * Retrieves a sprite from the cache using its key.
     * <p>
     * This is the primary method to get access to a sprite after it has been
     * loaded or registered.
     *
     * @param key The unique name of the sprite to retrieve.
     * @return The {@link Sprite} object associated with the key, or {@code null} if the key is not found.
     */
    public Sprite getSprite(String key) {
        if (!spriteCache.containsKey(key)) {
            System.err.println("Error: Sprite with key '" + key + "' not found in AssetManager.");
            return null;
        }
        return spriteCache.get(key);
    }
}
