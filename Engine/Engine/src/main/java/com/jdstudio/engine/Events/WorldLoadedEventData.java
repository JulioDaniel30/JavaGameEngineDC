package com.jdstudio.engine.Events;

import java.util.List;
import com.jdstudio.engine.Object.GameObject;
import com.jdstudio.engine.World.World;

/**
 * Contains the data for the {@code EngineEvent.WORLD_LOADED} event.
 * This event is triggered when a new game world has finished loading, and all its
 * GameObjects have been created and initialized.
 * Using a 'record' is a modern and concise way to create an immutable data class.
 * 
 * @param world       The loaded World instance.
 * @param gameObjects A list of all GameObjects present in the newly loaded world.
 * @author JDStudio
 */
public record WorldLoadedEventData(World world, List<GameObject> gameObjects) {}
