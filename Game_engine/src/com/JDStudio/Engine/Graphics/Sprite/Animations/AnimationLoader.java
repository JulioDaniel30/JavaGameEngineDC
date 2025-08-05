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
import com.JDStudio.Engine.Graphics.Sprite.Sprite;
import com.JDStudio.Engine.Graphics.Sprite.Spritesheet;
import com.JDStudio.Engine.Utils.ImageUtils;

public class AnimationLoader {

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
}