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
                System.err.println("Arquivo de diálogo não encontrado: " + path);
                return null;
            }
            
            String jsonText = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(jsonText);

            // --- LÓGICA ATUALIZADA PARA LER OS PONTOS DE ENTRADA ---

            // 1. Lê o ponto de entrada padrão (obrigatório)
            int defaultId = json.getInt("defaultEntryPoint");
            Dialogue dialogue = new Dialogue(defaultId);

            // 2. Lê os pontos de entrada condicionais (opcional)
            if (json.has("entryPoints")) {
                JSONArray entryPointsArray = json.getJSONArray("entryPoints");
                for (int i = 0; i < entryPointsArray.length(); i++) {
                    JSONObject entryJson = entryPointsArray.getJSONObject(i);
                    String condition = entryJson.getString("condition");
                    int nodeId = entryJson.getInt("nodeId");
                    dialogue.addEntryPoint(new DialogueEntryPoint(condition, nodeId));
                }
            }

            // 3. O resto do código para ler os nós permanece o mesmo, mas atualizado
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
                        
                        // Usa optString para ler as propriedades opcionais
                        String action = choiceJson.optString("action", null);
                        String condition = choiceJson.optString("condition", null);

                        // Passa todos os parâmetros para o construtor de DialogueChoice
                        node.addChoice(choiceText, nextNodeId, action, condition);
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