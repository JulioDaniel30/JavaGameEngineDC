package com.jdstudio.engine.Graphics.Sprite.Animations;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Graphics.AssetManager;
import com.jdstudio.engine.Graphics.Sprite.Sprite;
import com.jdstudio.engine.Graphics.Sprite.Spritesheet;
import com.jdstudio.engine.Utils.ImageUtils;

/**
 * A static utility class for loading animation data from JSON files.
 * It supports loading animations exported from Aseprite and a more generic JSON format.
 * 
 * @author JDStudio
 */
public class AnimationLoader {

	/**
     * Loads animations from a JSON file exported from Aseprite.
     * This method reads frame data and tags to automatically construct animations.
     * It can also create flipped versions of animations if specified.
     * <p>
     * Expected Aseprite JSON structure (simplified):
     * <pre>
     * {
     *   "frames": [
     *     { "filename": "frame_0.png", "frame": { "x": 0, "y": 0, "w": 32, "h": 32 }, "duration": 100 },
     *     // ... more frames
     *   ],
     *   "meta": {
     *     "frameTags": [
     *       { "name": "idle", "from": 0, "to": 3, "direction": "forward" },
     *       { "name": "walk_right", "from": 4, "to": 7, "direction": "forward" },
     *       // ... more tags
     *     ]
     *   }
     * }
     * </pre>
     *
     * @param jsonPath           The path to the Aseprite .json resource file.
     * @param sheet              The corresponding Spritesheet from which frames will be cropped.
     * @param createFlippedVersions If true, creates flipped versions for animations with "_right" suffix.
     * @return A Map containing the loaded animations, ready to be added to an Animator.
     */
    public static Map<String, Animation> loadFromAsepriteJson(String jsonPath, Spritesheet sheet, boolean createFlippedVersions) {
        Map<String, Animation> animations = new HashMap<>();

        try (InputStream is = AnimationLoader.class.getResourceAsStream(jsonPath)) {
            if (is == null) throw new Exception("Animation JSON file not found: " + jsonPath);

            String jsonText = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(jsonText);

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
                
                if (createFlippedVersions) {
                    String flippedName = null;

                    // First, check if the name ends with "_right"
                    if (animName.endsWith("_right")) {
                        flippedName = animName.replace("_right", "_left");
                    } 
                    // If not, check if it just ends with "right"
                    else if (animName.endsWith("right")) {
                        flippedName = animName.replace("right", "left");
                    }

                    // If one of the above conditions is true, flippedName will not be null
                    if (flippedName != null) {
                        List<Sprite> flippedFrames = new ArrayList<>();
                        for (Sprite frame : animFrames) {
                            flippedFrames.add(new Sprite(ImageUtils.flipHorizontal(frame.getImage())));
                        }
                        animations.put(flippedName, new Animation(speedInTicks, loop, flippedFrames.toArray(new Sprite[0])));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load animations from: " + jsonPath, e);
        }

        return animations;
    }

    /**
     * Loads a batch of animations from a generic JSON configuration file.
     * This method is an alternative to the Aseprite workflow and requires that individual sprites
     * (frames) have already been loaded into the AssetManager.
     * <p>
     * Expected JSON structure:
     * <pre>
     * {
     *   "animations": [
     *     {
     *       "key": "player_idle",
     *       "speed": 10,
     *       "loop": true,
     *       "frames": ["player_idle_0", "player_idle_1", "player_idle_2"]
     *     },
     *     {
     *       "key": "player_walk",
     *       "speed": 5,
     *       "loop": true,
     *       "frames": ["player_walk_0", "player_walk_1", "player_walk_2", "player_walk_3"]
     *     }
     *   ]
     * }
     * </pre>
     *
     * @param jsonPath The path to the .json resource file (e.g., "/configs/animations.json").
     * @param assets   The AssetManager containing all already loaded sprites, which will be referenced by 'keys' in the JSON.
     * @return A Map containing the loaded animations, ready to be added to an Animator.
     */
    public static Map<String, Animation> loadFromJson(String jsonPath, AssetManager assets) {
        Map<String, Animation> animations = new HashMap<>();

        try (InputStream is = AnimationLoader.class.getResourceAsStream(jsonPath)) {
            if (is == null) {
                System.err.println("Failed to find animation configuration file: " + jsonPath);
                return animations; // Return empty map
            }

            String jsonText = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject config = new JSONObject(jsonText);
            JSONArray animationsArray = config.getJSONArray("animations");

            for (int i = 0; i < animationsArray.length(); i++) {
                JSONObject animData = animationsArray.getJSONObject(i);
                String key = animData.getString("key");
                int speed = animData.getInt("speed");
                boolean loop = animData.optBoolean("loop", true); // By default, animation loops

                JSONArray framesArray = animData.getJSONArray("frames");
                List<Sprite> animFrames = new ArrayList<>();

                for (int j = 0; j < framesArray.length(); j++) {
                    String frameKey = framesArray.getString(j);
                    Sprite frameSprite = assets.getSprite(frameKey);
                    if (frameSprite != null) {
                        animFrames.add(frameSprite);
                    } else {
                        System.err.println("Warning: Sprite with key '" + frameKey + "' not found in AssetManager for animation '" + key + "'. Frame ignored.");
                    }
                }

                if (!animFrames.isEmpty()) {
                    Animation newAnimation = new Animation(speed, loop, animFrames.toArray(new Sprite[0]));
                    animations.put(key, newAnimation);
                } else {
                    System.err.println("Warning: No valid frames found for animation '" + key + "'. Animation not created.");
                }
            }
            System.out.println(animations.size() + " animations loaded successfully from: " + jsonPath);

        } catch (Exception e) {
            System.err.println("Error processing animation configuration file '" + jsonPath + "'. Check JSON format.");
            e.printStackTrace();
        }

        return animations;
    }
}
