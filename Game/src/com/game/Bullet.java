package com.game;

import org.json.JSONObject;

import com.JDStudio.Engine.Object.GameObject;

public class Bullet extends GameObject {
	public Bullet(JSONObject properties) {
        super(properties);
        this.sprite = PlayingState.assets.getSprite("bullet");
    }

	@Override
	public void tick() {
		/* Lógica do item aqui */ }
}