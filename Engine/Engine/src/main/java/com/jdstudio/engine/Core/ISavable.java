package com.jdstudio.engine.Core;

import org.json.JSONObject;

/**
 * Defines the contract for objects that can have their state saved and loaded.
 * Any class that needs to be persisted by the SaveManager should implement this interface.
 * 
 * @author JDStudio
 */
public interface ISavable {

    /**
     * Generates a JSONObject containing the current state of the object.
     * This object will be stored in the save file.
     * 
     * @return A JSONObject with the data to be saved.
     */
    JSONObject saveState();

    /**
     * Restores the state of the object from a JSONObject.
     * This method is called by the SaveManager when loading a game.
     * 
     * @param state The JSONObject containing the saved data.
     */
    void loadState(JSONObject state);
}
