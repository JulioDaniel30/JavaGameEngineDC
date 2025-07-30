package com.JDStudio.Engine.Object;

import java.awt.Graphics;

import org.json.JSONObject;

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Utils.PropertiesReader;

/**
 * Representa uma área invisível no jogo que pode acionar eventos
 * ou, neste caso, guiar outros GameObjects.
 */
public class TriggerZone extends GameObject {

    public String targetName; // O nome do objeto ao qual este gatilho está associado (ex: "door_1")

    public TriggerZone(JSONObject properties) {
        super(properties);
    }

    @Override
    public void initialize(JSONObject properties) {
        super.initialize(properties);
        PropertiesReader reader = new PropertiesReader(properties);
        // Lê a propriedade customizada do Tiled para saber a qual objeto pertence
        this.targetName = reader.getString("targetName", "");
        // Gatilhos não têm colisão física
        this.setCollisionType(CollisionType.TRIGGER);
        
        
        
        System.out.println(targetName);
    }
	@Override
	public void render(Graphics g) {
		renderDebug(g);
	}
    
    @Override
    public void renderDebug(Graphics g) {
    	// TODO Auto-generated method stub
    	super.renderDebug(g);
    	if (!Engine.isDebug) return;
    	g.setColor(new java.awt.Color(0, 255, 255, 100)); // Ciano semitransparente
        g.fillRect(getX() - Engine.camera.getX(), getY() - Engine.camera.getY(), width, height);
    }
    
}