package com.jdstudio.engine.Events;

import com.jdstudio.engine.Components.InteractionZone;
import com.jdstudio.engine.Object.GameObject;

/**
 * Contém os dados para os eventos de interação de zona.
 */
public record InteractionEventData(GameObject zoneOwner, GameObject target, InteractionZone zone) {}