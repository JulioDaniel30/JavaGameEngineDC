package com.jdstudio.engine.Components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Events.EngineEvent;
import com.jdstudio.engine.Events.EventManager;
import com.jdstudio.engine.Events.InteractionEventData;
import com.jdstudio.engine.Object.GameObject;

/**
 * Manages multiple interaction zones for a GameObject.
 * It triggers events when a target enters or leaves one of its zones.
 * This is useful for creating complex AI behaviors like aggro ranges, attack ranges, or dialogue triggers.
 * 
 * @author JDStudio
 */
public class InteractionComponent extends Component {

    /** The list of interaction zones managed by this component. */
    private List<InteractionZone> zones = new ArrayList<>();
    
    /** Maps a target GameObject to the list of zones it is currently inside. */
    private Map<GameObject, List<InteractionZone>> trackedTargets = new HashMap<>();

    /**
     * Constructs a new InteractionComponent.
     */
    public InteractionComponent() {
        // Empty constructor
    }

    /**
     * Adds an interaction zone to this component.
     * 
     * @param zone The InteractionZone to add.
     */
    public void addZone(InteractionZone zone) {
        this.zones.add(zone);
    }

    /**
     * The main method to check for interactions.
     * It iterates through potential targets and checks if they are inside any of the zones.
     * It fires TARGET_ENTERED_ZONE and TARGET_EXITED_ZONE events accordingly.
     * 
     * @param targets A list of GameObjects to check against (e.g., the player).
     */
    public void checkInteractions(List<GameObject> targets) {
        if (owner == null) return;

        // Update the position of all zones to follow the owner
        for (InteractionZone zone : zones) {
            zone.updatePosition();
        }

        for (GameObject target : targets) {
            if (target == owner) continue;

            List<InteractionZone> zonesTargetIsIn = trackedTargets.computeIfAbsent(target, k -> new ArrayList<>());
            double targetCenterX = target.getX() + target.getWidth() / 2.0;
            double targetCenterY = target.getY() + target.getHeight() / 2.0;

            for (InteractionZone zone : zones) {
                boolean isInside = zone.contains(targetCenterX, targetCenterY);
                boolean wasInside = zonesTargetIsIn.contains(zone);

                if (isInside && !wasInside) {
                    // Target has just entered the zone
                    zonesTargetIsIn.add(zone);
                    EventManager.getInstance().trigger(EngineEvent.TARGET_ENTERED_ZONE, new InteractionEventData(owner, target, zone));
                } else if (!isInside && wasInside) {
                    // Target has just exited the zone
                    zonesTargetIsIn.remove(zone);
                    EventManager.getInstance().trigger(EngineEvent.TARGET_EXITED_ZONE, new InteractionEventData(owner, target, zone));
                }
            }
        }
    }

    /**
     * Renders a debug visualization of the interaction zones.
     * Each zone type is rendered with a different color for easy identification.
     * This is only visible when Engine.isDebug is true.
     * 
     * @param g The Graphics context to draw on.
     */
    @Override
    public void render(Graphics g) {
        if (!Engine.isDebug || owner == null) return;

        Graphics2D g2d = (Graphics2D) g;
        AffineTransform originalTransform = g2d.getTransform();

        for (InteractionZone zone : zones) {
            Color debugColor;
            
            switch (zone.type) {
                case InteractionZone.TYPE_AGGRO:
                    debugColor = new Color(255, 255, 0, 70); // Yellow for aggro
                    break;
                case InteractionZone.TYPE_ATTACK:
                    debugColor = new Color(255, 0, 0, 70);   // Red for attack
                    break;
                case InteractionZone.TYPE_DIALOGUE:
                    debugColor = new Color(0, 255, 255, 70); // Cyan for dialogue
                    break;
                default:
                    debugColor = new Color(128, 128, 128, 70); // Gray for other types
                    break;
            }
            
            g2d.setColor(debugColor);
            
            Shape s = zone.getShape();
            
            // Apply camera transform to draw in world space
            g2d.translate(-Engine.camera.getX(), -Engine.camera.getY());
            g2d.fill(s);
            g2d.setTransform(originalTransform);
        }
    }
}
