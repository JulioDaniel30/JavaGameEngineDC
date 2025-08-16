// Arquivo: AssetManager.java
package com.JDStudio.Engine.Graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;

import com.JDStudio.Engine.Graphics.Sprite.Sprite;

/**
 * Gerencia o carregamento, armazenamento (cache) e acesso a todos os sprites do jogo.
 * <p>
 * Esta classe utiliza o padrão de centralizar o gerenciamento de recursos para
 * evitar o carregamento duplicado de imagens, economizando memória e melhorando a performance.
 * Garante que cada recurso seja identificado por uma chave de texto única.
 *
 * @author JDStudio
 * @since 1.0
 */
public class AssetManager {

    /** Cache para armazenar os sprites carregados, associando uma chave única a cada {@link Sprite}. */
    private Map<String, Sprite> spriteCache;

    /**
     * Construtor que inicializa o AssetManager.
     * Instancia o cache de sprites interno.
     */
    public AssetManager() {
        this.spriteCache = new HashMap<>();
    }
    
    /**
     * Carrega vários sprites a partir de um arquivo JSON de configuração.
     * O JSON deve conter um array "sprites" com objetos { "key", "path" }.
     *
     * @param jsonPath Caminho do arquivo JSON no classpath (ex: "/configs/sprites.json")
     */
    public void loadSpritesFromJson(String jsonPath) {
        try (InputStream is = getClass().getResourceAsStream(jsonPath)) {
            if (is == null) {
                System.err.println("Falha ao encontrar o arquivo de configuração de sprites: " + jsonPath);
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
            System.out.println(spritesArray.length() + " sprites carregados com sucesso de: " + jsonPath);
        } catch (Exception e) {
            System.err.println("Erro ao processar o arquivo de configuração de sprites '" + jsonPath + "'.");
            e.printStackTrace();
        }
    }

    /**
     * Carrega um sprite a partir de um arquivo de imagem e o armazena no cache.
     * <p>
     * Se um sprite com a mesma chave já existir no cache, a operação é ignorada.
     * Em caso de falha no carregamento, uma mensagem de erro é exibida no console,
     * mas a aplicação não é interrompida.
     *
     * @param key  O nome único (chave) para este sprite (ex: "player", "bullet").
     * @param path O caminho para o recurso de imagem, acessível pelo Classpath (ex: "/sprites/player.png").
     */
    public void loadSprite(String key, String path) {
        if (spriteCache.containsKey(key)) {
            return; // Otimização: não carrega se já existir.
        }
        try {
            BufferedImage image = ImageIO.read(getClass().getResource(path));
            spriteCache.put(key, new Sprite(image));
        } catch (IOException | IllegalArgumentException | NullPointerException e) {
            System.err.println("Falha ao carregar o sprite '" + key + "' do caminho: " + path);
            e.printStackTrace(); // Útil para depuração detalhada
        }
    }

    /**
     * Registra um objeto {@link Sprite} pré-existente no cache.
     * <p>
     * Este método é ideal para adicionar sprites que foram criados a partir de
     * uma {@code Spritesheet}, onde uma única imagem é fatiada em vários sprites.
     *
     * @param key    O nome único (chave) para este sprite.
     * @param sprite O objeto {@code Sprite} a ser adicionado ao cache.
     */
    public void registerSprite(String key, Sprite sprite) {
        // A verificação de nulidade adiciona robustez
        if (sprite == null) {
            System.err.println("Tentativa de registrar um sprite nulo com a chave: '" + key + "'");
            return;
        }
        if (spriteCache.containsKey(key)) {
            System.err.println("Aviso: A chave '" + key + "' já existe no cache. O sprite não será substituído.");
            return;
        }
        spriteCache.put(key, sprite);
    }

    /**
     * Recupera um sprite do cache usando sua chave.
     * <p>
     * Este é o método principal para obter acesso a um sprite após ele ter sido
     * carregado ou registrado.
     *
     * @param key O nome único do sprite a ser recuperado.
     * @return O objeto {@link Sprite} associado à chave, ou {@code null} se a chave não for encontrada.
     */
    public Sprite getSprite(String key) {
        if (!spriteCache.containsKey(key)) {
            System.err.println("Erro: O sprite com a chave '" + key + "' não foi encontrado no AssetManager.");
            return null;
        }
        return spriteCache.get(key);
    }
}