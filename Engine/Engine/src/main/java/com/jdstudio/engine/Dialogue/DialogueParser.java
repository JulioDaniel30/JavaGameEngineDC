package com.jdstudio.engine.Dialogue;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An engine-side utility class for parsing dialogue files in JSON format
 * and transforming them into Dialogue objects.
 * 
 * @author JDStudio
 */
public class DialogueParser {

    /**
     * Parses a JSON dialogue file and converts it into a Dialogue object.
     * The JSON file should have a specific structure:
     * - "defaultEntryPoint": (int) The ID of the starting node.
     * - "entryPoints": (JSONArray, optional) A list of conditional entry points.
     *   - Each entry point has a "condition" (String) and a "nodeId" (int).
     * - "nodes": (JSONArray) The list of all dialogue nodes.
     *   - Each node has an "id", "speakerName", "text", and an optional "choices" array.
     *   - Each choice has "text", "nextNodeId", and optional "action" and "condition" strings.
     * 
     * @param path The path to the .json resource file (e.g., "/dialogues/npc1.json").
     * @return A populated Dialogue object, or null if an error occurs.
     */
    public static Dialogue parseDialogue(String path) {
        try (InputStream is = DialogueParser.class.getResourceAsStream(path)) {
            if (is == null) {
                System.err.println("Dialogue file not found: " + path);
                return null;
            }
            
            String jsonText = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(jsonText);

            // 1. Read the default entry point (mandatory)
            int defaultId = json.getInt("defaultEntryPoint");
            Dialogue dialogue = new Dialogue(defaultId);

            // 2. Read conditional entry points (optional)
            if (json.has("entryPoints")) {
                JSONArray entryPointsArray = json.getJSONArray("entryPoints");
                for (int i = 0; i < entryPointsArray.length(); i++) {
                    JSONObject entryJson = entryPointsArray.getJSONObject(i);
                    String condition = entryJson.getString("condition");
                    int nodeId = entryJson.getInt("nodeId");
                    dialogue.addEntryPoint(new DialogueEntryPoint(condition, nodeId));
                }
            }

            // 3. Read all the dialogue nodes
            JSONArray nodesArray = json.getJSONArray("nodes");
            for (int i = 0; i < nodesArray.length(); i++) {
                JSONObject nodeJson = nodesArray.getJSONObject(i);
                int id = nodeJson.getInt("id");
                String speakerName = nodeJson.getString("speakerName");
                String text = nodeJson.getString("text");

                DialogueNode node = new DialogueNode(id, speakerName, text);

                if (nodeJson.has("choices")) {
                    JSONArray choicesArray = nodeJson.getJSONArray("choices");
                    for (int j = 0; j < choicesArray.length(); j++) {
                        JSONObject choiceJson = choicesArray.getJSONObject(j);
                        String choiceText = choiceJson.getString("text");
                        int nextNodeId = choiceJson.getInt("nextNodeId");
                        
                        // Use optString to read optional properties
                        String action = choiceJson.optString("action", null);
                        String condition = choiceJson.optString("condition", null);

                        node.addChoice(choiceText, nextNodeId, action, condition);
                    }
                }
                dialogue.addNode(node);
            }
            
            return dialogue;

        } catch (Exception e) {
            System.err.println("Failed to parse dialogue from: " + path);
            e.printStackTrace();
            return null;
        }
    }
}
