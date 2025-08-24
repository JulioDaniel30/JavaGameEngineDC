package com.jdstudio.engine.Dialogue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jdstudio.engine.Object.GameObject;

/**
 * Represents a single dialogue tree, containing all its nodes, choices, and entry points.
 * It determines the correct starting node based on game conditions.
 * 
 * @author JDStudio
 */
public class Dialogue {

    /** A map of all nodes in the dialogue, keyed by their unique ID. */
    private final Map<Integer, DialogueNode> nodes = new HashMap<>();
    
    /** The ID of the default node to start the dialogue from if no conditions are met. */
    private final int defaultEntryPointId;
    
    /** A list of conditional entry points that can change the starting node. */
    private final List<DialogueEntryPoint> entryPoints;

    /**
     * Constructs a new Dialogue.
     *
     * @param defaultEntryPointId The ID of the node to start from by default.
     */
    public Dialogue(int defaultEntryPointId) {
        this.defaultEntryPointId = defaultEntryPointId;
        this.entryPoints = new ArrayList<>();
    }

    /**
     * Adds a node to the dialogue.
     * @param node The DialogueNode to add.
     */
    public void addNode(DialogueNode node) { 
        this.nodes.put(node.id, node); 
    }

    /**
     * Adds a conditional entry point to the dialogue.
     * @param entryPoint The DialogueEntryPoint to add.
     */
    public void addEntryPoint(DialogueEntryPoint entryPoint) { 
        this.entryPoints.add(entryPoint); 
    }

    /**
     * Gets a specific node by its ID.
     * @param id The ID of the node.
     * @return The DialogueNode, or null if not found.
     */
    public DialogueNode getNode(int id) { 
        return nodes.get(id); 
    }

    /**
     * Gets the map of all nodes in the dialogue.
     * @return A map of all dialogue nodes.
     */
    public Map<Integer, DialogueNode> getNodes() { 
        return nodes; 
    }

    /**
     * Determines the correct starting node based on the current game state.
     * It checks conditional entry points first, and falls back to the default if no conditions are met.
     *
     * @param interactor The GameObject that is interacting (e.g., the player).
     * @return The DialogueNode where the conversation should begin.
     */
    public DialogueNode getStartNode(GameObject interactor) {
        ConditionManager conditionManager = ConditionManager.getInstance();
        
        // Check conditional entry points first
        for (DialogueEntryPoint entry : entryPoints) {
            // Delegate the condition check to the ConditionManager
            if (conditionManager.checkCondition(entry.condition(), interactor)) {
                return getNode(entry.nodeId());
            }
        }
        
        // If no conditions are met, use the default
        return getNode(defaultEntryPointId);
    }
}
