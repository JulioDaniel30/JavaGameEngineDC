package com.game.Tiles;

import com.jdstudio.engine.Graphics.Sprite.Sprite;
import com.jdstudio.engine.World.Tile;

public class WallTile extends Tile {
	public WallTile(int x, int y, Sprite sprite) {
		super(x, y, sprite);
		tileType = TileType.SOLID;

	}
}