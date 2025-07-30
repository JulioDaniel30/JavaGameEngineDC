package com.game;

import org.json.JSONObject;

import com.JDStudio.Engine.Object.GameObject;

public class Weapon extends GameObject {
	public Weapon(JSONObject properties) {
        super(properties);
        this.sprite = PlayingState.assets.getSprite("weapon");
    }

	@Override
	public void tick() {
		/* Lógica da arma aqui */ }
}