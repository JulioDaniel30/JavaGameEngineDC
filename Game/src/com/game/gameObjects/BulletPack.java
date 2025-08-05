package com.game.gameObjects;

import org.json.JSONObject;

import com.JDStudio.Engine.Object.GameObject;
import com.game.States.PlayingState;

public class BulletPack extends GameObject {
	public BulletPack(JSONObject properties) {
        super(properties);
        this.sprite = PlayingState.assets.getSprite("bullet_pack");
        setCollisionType(CollisionType.TRIGGER);
    }

	@Override
	public void tick() {
		/* Lógica do item aqui */ }
}