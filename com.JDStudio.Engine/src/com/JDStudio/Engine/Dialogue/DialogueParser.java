// engine
package com.JDStudio.Engine.Dialogue;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Uma classe utilitária "engine side" para analisar (parse) arquivos de diálogo
 * em formato JSON e transformá-los em objetos Dialogue.
 */
public class DialogueParser {

    /**
     * Analisa um arquivo JSON de diálogo e o converte em um objeto Dialogue.
     * @param path O caminho para o recurso do arquivo .json (ex: "/dialogues/npc1.json").
     * @return Um objeto Dialogue preenchido, ou null se ocorrer um erro.
     */
    public static Dialogue parseDialogue(String path) {
        try (InputStream is = DialogueParser.class.getResourceAsStream(path)) {
            if (is == null) {
                throw new Exception("Arquivo de diálogo não encontrado: " + path);
            }
            
            String jsonText = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(jsonText);

            int startNodeId = json.getInt("startNodeId");
            Dialogue dialogue = new Dialogue(startNodeId);

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
                        
                        // Lê a ação se ela existir; caso contrário, a ação é nula.
                        String action = choiceJson.has("action") ? choiceJson.getString("action") : null;

                        node.addChoice(choiceText, nextNodeId, action);
                    }
                }
                dialogue.addNode(node);
            }
            
            return dialogue;

        } catch (Exception e) {
            System.err.println("Falha ao analisar o diálogo de: " + path);
            e.printStackTrace();
            return null;
        }
    }
}