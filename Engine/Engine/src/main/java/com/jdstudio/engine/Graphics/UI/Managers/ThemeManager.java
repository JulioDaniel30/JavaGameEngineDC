package com.jdstudio.engine.Graphics.UI.Managers;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import com.jdstudio.engine.Graphics.Sprite.Sprite;
import com.jdstudio.engine.Graphics.UI.UISpriteKey;
import com.jdstudio.engine.Graphics.UI.UITheme;

/**
 * A singleton class that manages UI themes, loading and caching sprites
 * based on the currently active theme. This allows for easy switching of UI skins.
 * 
 * @author JDStudio
 */
public class ThemeManager {
    
    private static final ThemeManager instance = new ThemeManager();
    
    /** A nested cache: Map<Theme, Map<Key, Sprite>> to store loaded sprites per theme. */
    private final Map<UITheme, Map<UISpriteKey, Sprite>> themeCache = new HashMap<>();
    
    /** The currently active UI theme. */
    private UITheme currentTheme = UITheme.MEDIEVAL;

    private ThemeManager() {}

    /**
     * Gets the single instance of the ThemeManager.
     * @return The singleton instance.
     */
    public static ThemeManager getInstance() {
        return instance;
    }

    /**
     * Sets the active UI theme for the game.
     * @param theme The theme to be used (e.g., UITheme.SCI_FI).
     */
    public void setTheme(UITheme theme) {
        System.out.println("UI Theme set to: " + theme.name());
        this.currentTheme = theme;
    }

    /**
     * Retrieves a sprite from the currently active theme.
     * It loads and caches the sprite if it's the first access for that theme and key.
     * 
     * @param key The key of the desired sprite (e.g., UISpriteKey.BUTTON_NORMAL).
     * @return The corresponding Sprite.
     * @throws RuntimeException if the sprite file cannot be found or loaded.
     */
    public Sprite get(UISpriteKey key) {
        // Ensure the map for the current theme exists in the cache
        themeCache.computeIfAbsent(currentTheme, k -> new HashMap<>());
        
        // Try to get the sprite from the cache
        Sprite cachedSprite = themeCache.get(currentTheme).get(key);
        if (cachedSprite != null) {
            return cachedSprite;
        }

        // If not in cache, load the sprite
        String path = buildSpritePath(currentTheme, key);
        try {
            BufferedImage image = ImageIO.read(getClass().getResourceAsStream(path));
            if (image == null) {
                throw new IOException("Sprite file not found for theme: " + path);
            }
            Sprite newSprite = new Sprite(image);
            
            // Store the new sprite in the cache
            themeCache.get(currentTheme).put(key, newSprite);
            
            return newSprite;
        } catch (Exception e) {
            System.err.println("Critical failure to load theme sprite: " + path);
            e.printStackTrace();
            // Throw a runtime exception to stop the game, as the UI cannot be built
            throw new RuntimeException(e);
        }
    }

    /**
     * Constructs the file path for a UI sprite based on the theme and sprite key.
     * Example: getPath(UITheme.MEDIEVAL, UISpriteKey.BUTTON_NORMAL) -> "/ui/medieval/button_normal.png"
     * 
     * @param theme The UITheme.
     * @param key   The UISpriteKey.
     * @return The constructed file path.
     */
    private String buildSpritePath(UITheme theme, UISpriteKey key) {
        String themeName = theme.name().toLowerCase(); // e.g., "medieval"
        String keyName = key.name().toLowerCase();   // e.g., "button_normal"
        return "/Engine/UI/" + themeName + "/" + keyName + ".png";
    }
}
