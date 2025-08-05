package com.game.gameObjects;

import org.json.JSONObject;

import com.JDStudio.Engine.Object.GameObject;
import com.game.States.PlayingState;

public class Bullet extends GameObject {
	public Bullet(JSONObject properties) {
        super(properties);
        this.sprite = PlayingState.assets.getSprite("bullet");
    }

	@Override
	public void tick() {
		/* Lógica do item aqui */ }
}