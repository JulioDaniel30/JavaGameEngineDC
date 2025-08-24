package com.jdstudio.engine.Events;

import com.jdstudio.engine.Components.InteractionZone;
import com.jdstudio.engine.Object.GameObject;

/**
 * Contains the data for zone interaction events, such as {@code EngineEvent.TARGET_ENTERED_ZONE}
 * and {@code EngineEvent.TARGET_EXITED_ZONE}.
 * 
 * @param zoneOwner The GameObject that owns the InteractionZone.
 * @param target    The GameObject that entered or exited the zone (e.g., the player).
 * @param zone      The InteractionZone involved in the event.
 * @author JDStudio
 */
public record InteractionEventData(GameObject zoneOwner, GameObject target, InteractionZone zone) {}
