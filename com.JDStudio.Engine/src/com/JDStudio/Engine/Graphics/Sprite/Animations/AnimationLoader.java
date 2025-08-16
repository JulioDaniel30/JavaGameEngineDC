package com.JDStudio.Engine.Graphics.Sprite.Animations;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Graphics.AssetManager;
import com.JDStudio.Engine.Graphics.Sprite.Sprite;
import com.JDStudio.Engine.Graphics.Sprite.Spritesheet;
import com.JDStudio.Engine.Utils.ImageUtils;

public class AnimationLoader {

	/**
     * Carrega animações a partir de um arquivo JSON exportado do Aseprite.
     * Este método lê os dados de frames e tags para construir as animações automaticamente.
     * @param jsonPath O caminho para o recurso do arquivo .json do Aseprite.
     * @param sheet A Spritesheet correspondente de onde os frames serão recortados.
     * @param createFlippedVersions Se true, cria versões espelhadas para animações com sufixo "_right".
     * @return Um Map contendo as animações carregadas, prontas para serem adicionadas a um Animator.
     */
    public static Map<String, Animation> loadFromAsepriteJson(String jsonPath, Spritesheet sheet, boolean createFlippedVersions) {
        Map<String, Animation> animations = new HashMap<>();

        try (InputStream is = AnimationLoader.class.getResourceAsStream(jsonPath)) {
            if (is == null) throw new Exception("Arquivo JSON de animação não encontrado: " + jsonPath);

            String jsonText = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(jsonText);

            // --- 1ª MUDANÇA AQUI: Lê "frames" como um JSONArray ---
            JSONArray framesArray = json.getJSONArray("frames");
            
            JSONObject metaJson = json.getJSONObject("meta");
            JSONArray frameTags = metaJson.getJSONArray("frameTags");

            for (int i = 0; i < frameTags.length(); i++) {
                JSONObject tag = frameTags.getJSONObject(i);
                String animName = tag.getString("name");
                int from = tag.getInt("from");
                int to = tag.getInt("to");
                boolean loop = !tag.getString("direction").equals("once");

                List<Sprite> animFrames = new ArrayList<>();
                for (int j = from; j <= to; j++) {
                    // --- 2ª MUDANÇA AQUI: Pega o frame da lista pelo seu índice 'j' ---
                    JSONObject frameData = framesArray.getJSONObject(j);
                    
                    JSONObject rect = frameData.getJSONObject("frame");
                    int x = rect.getInt("x");
                    int y = rect.getInt("y");
                    int w = rect.getInt("w");
                    int h = rect.getInt("h");
                    
                    animFrames.add(sheet.getSprite(x, y, w, h));
                }

                int speedInTicks = (int) (framesArray.getJSONObject(from).getInt("duration") / (1000.0 / Engine.getFPS()));
                
                animations.put(animName, new Animation(speedInTicks, loop, animFrames.toArray(new Sprite[0])));
                
                if (createFlippedVersions && animName.contains("_right")) {
                    String flippedName = animName.replace("_right", "_left");
                    List<Sprite> flippedFrames = new ArrayList<>();
                    for (Sprite frame : animFrames) {
                        flippedFrames.add(new Sprite(ImageUtils.flipHorizontal(frame.getImage())));
                    }
                    animations.put(flippedName, new Animation(speedInTicks, loop, flippedFrames.toArray(new Sprite[0])));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Falha ao carregar animações de: " + jsonPath, e);
        }

        return animations;
    }
    /**
     * **NOVO MÉTODO**
     * Carrega um lote de animações a partir de um arquivo de configuração JSON genérico.
     * Este método é uma alternativa ao fluxo de trabalho do Aseprite e requer que os sprites
     * individuais (frames) já tenham sido carregados no AssetManager.
     *
     * @param jsonPath O caminho para o recurso do arquivo .json (ex: "/configs/animations.json").
     * @param assets   O AssetManager que contém todos os sprites já carregados, que serão referenciados pelas 'keys' no JSON.
     * @return Um Map contendo as animações carregadas, prontas para serem adicionadas a um Animator.
     */
    public static Map<String, Animation> loadFromJson(String jsonPath, AssetManager assets) {
        Map<String, Animation> animations = new HashMap<>();

        try (InputStream is = AnimationLoader.class.getResourceAsStream(jsonPath)) {
            if (is == null) {
                System.err.println("Falha ao encontrar o arquivo de configuração de animações: " + jsonPath);
                return animations; // Retorna o mapa vazio
            }

            String jsonText = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject config = new JSONObject(jsonText);
            JSONArray animationsArray = config.getJSONArray("animations");

            for (int i = 0; i < animationsArray.length(); i++) {
                JSONObject animData = animationsArray.getJSONObject(i);
                String key = animData.getString("key");
                int speed = animData.getInt("speed");
                boolean loop = animData.optBoolean("loop", true); // Por padrão, a animação é em loop

                JSONArray framesArray = animData.getJSONArray("frames");
                List<Sprite> animFrames = new ArrayList<>();

                for (int j = 0; j < framesArray.length(); j++) {
                    String frameKey = framesArray.getString(j);
                    Sprite frameSprite = assets.getSprite(frameKey);
                    if (frameSprite != null) {
                        animFrames.add(frameSprite);
                    } else {
                        System.err.println("Aviso: Sprite com a chave '" + frameKey + "' não encontrado no AssetManager para a animação '" + key + "'. Frame ignorado.");
                    }
                }

                if (!animFrames.isEmpty()) {
                    Animation newAnimation = new Animation(speed, loop, animFrames.toArray(new Sprite[0]));
                    animations.put(key, newAnimation);
                } else {
                    System.err.println("Aviso: Nenhuma frame válido encontrado para a animação '" + key + "'. A animação não foi criada.");
                }
            }
            System.out.println(animations.size() + " animações carregadas com sucesso de: " + jsonPath);

        } catch (Exception e) {
            System.err.println("Erro ao processar o arquivo de configuração de animações '" + jsonPath + "'. Verifique o formato do JSON.");
            e.printStackTrace();
        }

        return animations;
    }
}