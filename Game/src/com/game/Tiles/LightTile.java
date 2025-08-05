package com.game.Tiles;

import com.JDStudio.Engine.Graphics.Lighting.Light;
import com.JDStudio.Engine.Graphics.Lighting.LightingManager;
import com.JDStudio.Engine.Graphics.Sprite.Sprite;
import com.JDStudio.Engine.World.Tile;

/**
 * Representa um tile que também emite uma fonte de luz.
 */
public class LightTile extends Tile {

	private Light associatedLight;

	/**
	 * Cria um novo tile que emite luz.
	 * 
	 * @param x      Posição X do tile no mundo (em pixels).
	 * @param y      Posição Y do tile no mundo (em pixels).
	 * @param sprite O sprite do tile.
	 * @param light  As propriedades da luz a ser emitida (raio, cor).
	 */
	public LightTile(int x, int y, Sprite sprite, Light light) {
		super(x, y, sprite);
		this.associatedLight = light;
		this.isSolid = false;
		// Define a posição da luz para o centro do tile
		// (Sua classe Tile tem um typo 'heigth', estou mantendo para ser compatível)
		this.associatedLight.x = x + (this.width / 2.0);
		this.associatedLight.y = y + (this.heigth / 2.0);

		// A mágica acontece aqui: adiciona a luz ao sistema de iluminação
		LightingManager.getInstance().addLight(this.associatedLight);
	}

	/**
	 * Cria um novo tile que emite luz, com colisão opcional.
	 * 
	 * @param x       Posição X do tile no mundo (em pixels).
	 * @param y       Posição Y do tile no mundo (em pixels).
	 * @param sprite  O sprite do tile.
	 * @param light   As propriedades da luz a ser emitida (raio, cor).
	 * @param isSolid Define se este tile deve bloquear o movimento.
	 */
	public LightTile(int x, int y, Sprite sprite, Light light, boolean isSolid) {
		super(x, y, sprite);

		// --- A LÓGICA DE COLISÃO ESTÁ AQUI ---
		this.isSolid = isSolid;

		this.associatedLight = light;

		this.associatedLight.x = x + (this.width / 2.0);
		this.associatedLight.y = y + (this.heigth / 2.0);

		LightingManager.getInstance().addLight(this.associatedLight);
	}

	/**
	 * (Funcionalidade futura) Se o tile for destruído, remove a luz associada.
	 */
	public void destroy() {
		if (this.associatedLight != null) {
			LightingManager.getInstance().removeLight(this.associatedLight);
			this.associatedLight = null;
		}
		// Aqui entraria a lógica para remover o tile do mundo.
	}
}