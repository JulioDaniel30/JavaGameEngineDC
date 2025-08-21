// Arquivo: AssetManager.java
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
     * **NOVO MÉTODO**
     * Carrega e recorta múltiplos sprites de uma única spritesheet, com base
     * em definições de um arquivo de configuração JSON.
     * Suporta definições baseadas em grelha (grid) e coordenadas manuais.
     *
     * @param jsonPath O caminho para o recurso do arquivo .json (ex: "/configs/player_sprites.json").
     */
    public void loadSpritesFromSpritesheetJson(String jsonPath) {
        try (InputStream is = getClass().getResourceAsStream(jsonPath)) {
            if (is == null) {
                System.err.println("Falha ao encontrar o arquivo de definição de spritesheet: " + jsonPath);
                return;
            }

            String jsonText = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject config = new JSONObject(jsonText);

            // 1. Carrega a spritesheet principal
            String sheetPath = config.getString("spritesheetPath");
            Spritesheet sheet = new Spritesheet(sheetPath);

            JSONArray definitions = config.getJSONArray("definitions");
            int spritesRegistered = 0;

            // 2. Itera sobre cada bloco de definição (grid ou manual)
            for (int i = 0; i < definitions.length(); i++) {
                JSONObject def = definitions.getJSONObject(i);
                String type = def.getString("type");

                if ("grid".equals(type)) {
                	 String prefix = def.getString("prefix");
                     int spriteWidth = def.getInt("spriteWidth");
                     int spriteHeight = def.getInt("spriteHeight");
                     int startX = def.optInt("startX", 0);
                     int startY = def.optInt("startY", 0);

                     // --- LÓGICA ATUALIZADA ---

                     // 1. Lê o 'startIndex' opcional. O padrão é 1 para manter a compatibilidade.
                     int startIndex = def.optInt("startIndex", 1);

                     // Se tiver numCols e numRows, usa a nova lógica 2D
                     if (def.has("numCols") && def.has("numRows")) {
                         int numCols = def.getInt("numCols");
                         int numRows = def.getInt("numRows");
                         
                         // 2. Inicia o contador com o startIndex
                         int counter = startIndex;
                         for (int row = 0; row < numRows; row++) {
                             for (int col = 0; col < numCols; col++) {
                                 String key = prefix + counter; // Usa o contador
                                 int x = startX + (col * spriteWidth);
                                 int y = startY + (row * spriteHeight);
                                 registerSprite(key, sheet.getSprite(x, y, spriteWidth, spriteHeight));
                                 spritesRegistered++;
                                 counter++;
                             }
                         }
                     } 
                     // Se não, se tiver 'count', usa a lógica antiga 1D
                     else if (def.has("count")) {
                         int count = def.getInt("count");
                         for (int j = 0; j < count; j++) {
                             // 3. Modifica a criação da chave para usar o startIndex
                             // Se j=0, a chave será prefix + startIndex. Se j=1, será prefix + startIndex + 1, e assim por diante.
                             String key = prefix + (startIndex + j);
                             int x = startX + (j * spriteWidth);
                             registerSprite(key, sheet.getSprite(x, startY, spriteWidth, spriteHeight));
                             spritesRegistered++;
                         }
                     }
                } else if ("full_grid".equals(type)) {
                    // --- A NOVA LÓGICA AUTOMÁTICA ESTÁ AQUI ---
                    String prefix = def.getString("prefix");
                    int spriteWidth = def.getInt("spriteWidth");
                    int spriteHeight = def.getInt("spriteHeight");
                    int startIndex = def.optInt("startIndex", 1);
                    
                    // Calcula automaticamente o número de colunas e linhas
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
                    // Lógica para extração manual
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
            System.out.println(spritesRegistered + " sprites recortados e registrados com sucesso de: " + sheetPath);

        } catch (Exception e) {
            System.err.println("Erro ao processar o arquivo de definição de spritesheet '" + jsonPath + "'. Verifique o formato do JSON.");
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