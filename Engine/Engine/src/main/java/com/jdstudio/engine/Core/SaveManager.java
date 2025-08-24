package com.jdstudio.engine.Core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONObject;

/**
 * A static utility class for handling the saving and loading of game state to and from JSON files.
 * It provides methods to write and read JSONObjects from a dedicated "saves" folder.
 * 
 * @author JDStudio
 */
public class SaveManager {

    /** The directory where save files are stored. */
    private static final String SAVE_FOLDER = "saves/";

    /**
     * Saves a JSONObject to a file in the saves folder.
     *
     * @param state    The game state to be saved.
     * @param fileName The name of the file (e.g., "savegame1.json").
     * @return true if the save was successful, false otherwise.
     */
    public static boolean saveToFile(JSONObject state, String fileName) {
        // Ensure the "saves" folder exists
        File dir = new File(SAVE_FOLDER);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try (FileWriter file = new FileWriter(SAVE_FOLDER + fileName)) {
            file.write(state.toString(4)); // The '4' formats the JSON to be human-readable
            System.out.println("Game saved to: " + fileName);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving game to: " + fileName);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Loads a JSONObject from a file in the saves folder.
     *
     * @param fileName The name of the file to be loaded.
     * @return The JSONObject with the game state, or null if it fails.
     */
    public static JSONObject loadFromFile(String fileName) {
        File saveFile = new File(SAVE_FOLDER + fileName);
        if (!saveFile.exists()) {
            System.out.println("Save file not found: " + fileName);
            return null;
        }

        try {
            String content = new String(Files.readAllBytes(Paths.get(saveFile.toURI())));
            System.out.println("Game loaded from: " + fileName);
            return new JSONObject(content);
        } catch (IOException e) {
            System.err.println("Error loading game from: " + fileName);
            e.printStackTrace();
            return null;
        }
    }
}
