package com.JDStudio.Game;
import com.JDStudio.Engine.Graphics.Sprite.Sprite;
import com.JDStudio.Engine.World.Tile;
public class WallTile extends Tile {
    public WallTile(int x, int y, Sprite sprite) {
        super(x, y, sprite);
        this.isSolid = true;
    }
}