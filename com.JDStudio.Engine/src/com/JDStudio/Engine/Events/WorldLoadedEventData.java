package com.JDStudio.Engine.Events;

import java.util.List;
import com.JDStudio.Engine.Object.GameObject;
import com.JDStudio.Engine.World.World;

/**
 * Contém os dados para o evento WORLD_LOADED.
 * Usar um 'record' é uma forma moderna e concisa de criar uma classe de dados imutável.
 */
public record WorldLoadedEventData(World world, List<GameObject> gameObjects) {}