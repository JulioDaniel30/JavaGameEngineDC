package com.game.gameObjects;

import org.json.JSONObject;

import com.jdstudio.engine.Object.GameObject;
import com.game.States.PlayingState;

public class Lifepack extends GameObject {
	public Lifepack(JSONObject properties) {
        super(properties);
        this.sprite = PlayingState.assets.getSprite("lifepack");
        setCollisionType(CollisionType.TRIGGER);
    }

	@Override
	public void tick() {
		/* Lógica do item aqui */ }
}