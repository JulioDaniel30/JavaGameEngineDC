package com.game;

import org.json.JSONObject;

import com.JDStudio.Engine.Object.GameObject;

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