package com.jdstudio.engine.Object;

import java.awt.Graphics;

import org.json.JSONObject;

import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Utils.PropertiesReader;

/**
 * Represents an invisible area in the game that can trigger events
 * or, in this case, guide other GameObjects.
 * Trigger zones have no physical collision but can detect when other objects enter them.
 * 
 * @author JDStudio
 */
public class TriggerZone extends GameObject {

    /** The name of the object to which this trigger is associated (e.g., "door_1"). */
    public String targetName;

    /**
     * Constructs a new TriggerZone.
     *
     * @param properties A JSONObject containing the initial properties of the trigger zone.
     */
    public TriggerZone(JSONObject properties) {
        super(properties);
    }

    /**
     * Initializes the TriggerZone's properties.
     * It reads the custom "targetName" property from Tiled and sets the collision type to TRIGGER.
     *
     * @param properties A JSONObject containing the properties to initialize.
     */
    @Override
    public void initialize(JSONObject properties) {
        super.initialize(properties);
        PropertiesReader reader = new PropertiesReader(properties);
        // Reads the custom property from Tiled to know which object it belongs to
        this.targetName = reader.getString("targetName", "");
        // Triggers have no physical collision
        this.setCollisionType(CollisionType.TRIGGER);
    }

    /**
     * Renders the TriggerZone. In normal mode, it does not render anything.
     * In debug mode, it calls {@code renderDebug}.
     *
     * @param g The Graphics context to draw on.
     */
	@Override
	public void render(Graphics g) {
		renderDebug(g);
	}
    
    /**
     * Renders debug information for the TriggerZone.
     * It draws a semi-transparent cyan rectangle representing the zone's bounds.
     * This method is only called if {@code Engine.isDebug} is true.
     *
     * @param g The Graphics context to draw on.
     */
    @Override
    public void renderDebug(Graphics g) {
    	super.renderDebug(g);
    	if (!Engine.isDebug) return;
    	g.setColor(new java.awt.Color(0, 255, 255, 100)); // Semi-transparent cyan
        g.fillRect(getX() - Engine.camera.getX(), getY() - Engine.camera.getY(), width, height);
    }
}
