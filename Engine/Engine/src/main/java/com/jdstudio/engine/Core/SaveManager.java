package com.jdstudio.engine.Core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONObject;

public class SaveManager {

    private static final String SAVE_FOLDER = "saves/";

    /**
     * Salva um JSONObject em um arquivo.
     * @param state O estado do jogo a ser salvo.
     * @param fileName O nome do arquivo (ex: "savegame1.json").
     * @return true se o salvamento foi bem-sucedido, false caso contrário.
     */
    public static boolean saveToFile(JSONObject state, String fileName) {
        // Garante que a pasta "saves" exista
        File dir = new File(SAVE_FOLDER);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try (FileWriter file = new FileWriter(SAVE_FOLDER + fileName)) {
            file.write(state.toString(4)); // O '4' formata o JSON para ser legível
            System.out.println("Jogo salvo em: " + fileName);
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao salvar o jogo em: " + fileName);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Carrega um JSONObject de um arquivo.
     * @param fileName O nome do arquivo a ser carregado.
     * @return O JSONObject com o estado do jogo, ou null se falhar.
     */
    public static JSONObject loadFromFile(String fileName) {
        File saveFile = new File(SAVE_FOLDER + fileName);
        if (!saveFile.exists()) {
            System.out.println("Arquivo de save não encontrado: " + fileName);
            return null;
        }

        try {
            String content = new String(Files.readAllBytes(Paths.get(saveFile.toURI())));
            System.out.println("Jogo carregado de: " + fileName);
            return new JSONObject(content);
        } catch (IOException e) {
            System.err.println("Erro ao carregar o jogo de: " + fileName);
            e.printStackTrace();
            return null;
        }
    }
}