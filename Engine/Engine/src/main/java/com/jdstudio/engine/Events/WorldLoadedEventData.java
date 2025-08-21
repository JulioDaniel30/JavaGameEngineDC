package com.jdstudio.engine.Events;

import java.util.List;
import com.jdstudio.engine.Object.GameObject;
import com.jdstudio.engine.World.World;

/**
 * Contém os dados para o evento WORLD_LOADED.
 * Usar um 'record' é uma forma moderna e concisa de criar uma classe de dados imutável.
 */
public record WorldLoadedEventData(World world, List<GameObject> gameObjects) {}