package com.JDStudio.Engine.Events;

import com.JDStudio.Engine.Components.InteractionZone;
import com.JDStudio.Engine.Object.GameObject;

/**
 * Contém os dados para os eventos de interação de zona.
 */
public record InteractionEventData(GameObject zoneOwner, GameObject target, InteractionZone zone) {}