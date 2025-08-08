package com.JDStudio.Engine.Graphics.UI.Managers;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import com.JDStudio.Engine.Graphics.Sprite.Sprite;
import com.JDStudio.Engine.Graphics.UI.UISpriteKey;
import com.JDStudio.Engine.Graphics.UI.UITheme;

public class ThemeManager {
    
    private static final ThemeManager instance = new ThemeManager();
    
    // Um cache aninhado: Map<Tema, Map<Chave, Sprite>>
    private final Map<UITheme, Map<UISpriteKey, Sprite>> themeCache = new HashMap<>();
    
    // Define um tema padrão
    private UITheme currentTheme = UITheme.MEDIEVAL;

    private ThemeManager() {}

    public static ThemeManager getInstance() {
        return instance;
    }

    /**
     * Define o tema de UI ativo para o jogo.
     * @param theme O tema a ser usado (ex: UITheme.SCI_FI).
     */
    public void setTheme(UITheme theme) {
        System.out.println("Tema de UI definido para: " + theme.name());
        this.currentTheme = theme;
    }

    /**
     * Pega um sprite do tema atualmente ativo.
     * Carrega e armazena em cache o sprite se for o primeiro acesso.
     * @param key A chave do sprite desejado (ex: UISpriteKey.BUTTON_NORMAL).
     * @return O Sprite correspondente.
     */
    public Sprite get(UISpriteKey key) {
        // Garante que o mapa para o tema atual exista no cache
        themeCache.computeIfAbsent(currentTheme, k -> new HashMap<>());
        
        // Tenta pegar o sprite do cache
        Sprite cachedSprite = themeCache.get(currentTheme).get(key);
        if (cachedSprite != null) {
            return cachedSprite;
        }

        // Se não estiver no cache, carrega o sprite
        String path = buildSpritePath(currentTheme, key);
        try {
            BufferedImage image = ImageIO.read(getClass().getResourceAsStream(path));
            if (image == null) {
                throw new IOException("Arquivo de sprite não encontrado para o tema: " + path);
            }
            Sprite newSprite = new Sprite(image);
            
            // Armazena o novo sprite no cache
            themeCache.get(currentTheme).put(key, newSprite);
            
            return newSprite;
        } catch (Exception e) {
            System.err.println("Falha crítica ao carregar o sprite do tema: " + path);
            e.printStackTrace();
            // Lança uma exceção para parar o jogo, pois a UI não pode ser construída
            throw new RuntimeException(e);
        }
    }

    /**
     * Constrói o caminho do arquivo de forma padronizada.
     * Ex: getPath(UITheme.MEDIEVAL, UISpriteKey.BUTTON_NORMAL) -> "/ui/medieval/button_normal.png"
     */
    private String buildSpritePath(UITheme theme, UISpriteKey key) {
        String themeName = theme.name().toLowerCase(); // ex: "medieval"
        String keyName = key.name().toLowerCase();   // ex: "button_normal"
        return "/Engine/UI/" + themeName + "/" + keyName + ".png";
    }
}