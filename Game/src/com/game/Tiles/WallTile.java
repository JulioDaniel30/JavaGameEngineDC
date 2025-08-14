package com.game.Tiles;

import com.JDStudio.Engine.Graphics.Sprite.Sprite;
import com.JDStudio.Engine.World.Tile;

public class WallTile extends Tile {
	public WallTile(int x, int y, Sprite sprite) {
		super(x, y, sprite);
		tileType = TileType.SOLID;

	}
}