package com.jdstudio.engine.Core;

import java.util.HashSet;
import java.util.Set;

/**
 * A Singleton that manages the global state of the game through "flags".
 * Flags are simple string markers that indicate an event has occurred or a condition has been met
 * (e.g., a quest has been accepted, a door has been unlocked).
 * This allows for a decoupled way to check for game-wide states.
 * 
 * @author JDStudio
 */
public class GameStateManager {
    private static final GameStateManager instance = new GameStateManager();
    private Set<String> flags = new HashSet<>();

    private GameStateManager() {}

    /**
     * Gets the single instance of the GameStateManager.
     * @return The singleton instance.
     */
    public static GameStateManager getInstance() { 
        return instance; 
    }

    /**
     * Adds a flag to the game state.
     * @param flag The string identifier for the flag to add.
     */
    public void setFlag(String flag) { 
        flags.add(flag); 
    }
    
    /**
     * Removes a flag from the game state.
     * @param flag The string identifier for the flag to remove.
     */
    public void removeFlag(String flag) { 
        flags.remove(flag); 
    }
    
    /**
     * Checks if a specific flag is currently active.
     * @param flag The string identifier for the flag to check.
     * @return true if the flag is set, false otherwise.
     */
    public boolean hasFlag(String flag) { 
        return flags.contains(flag); 
    }
    
    /**
     * Clears all flags from the game state.
     * Useful for starting a new game or loading from a save.
     */
    public void clearFlags() { 
        flags.clear(); 
    }

    /**
     * Gets the complete set of current flags.
     * This is primarily used by the SaveManager to save the game state.
     * @return A Set containing all current flags.
     */
    public Set<String> getFlags() {
        return this.flags;
    }

    /**
     * Sets the complete set of flags.
     * This is primarily used by the SaveManager to load the game state.
     * @param flags A Set of flags to load.
     */
    public void setFlags(Set<String> flags) {
        this.flags = flags;
    }
}
