package com.JDStudio.Engine.Graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;


/**
 * Representa uma folha de sprites (spritesheet) que pode ser carregada
 * de um arquivo de imagem. Esta classe facilita a extração de sprites
 * individuais da imagem principal.
 *
 * @author JD Studio
 * @version 1.0
 */
public class Spritesheet {
	
	private BufferedImage spritesheet;
	/**
     * Carrega a imagem da spritesheet a partir do caminho especificado.
     * O caminho deve ser relativo à pasta de recursos (resources).
     * @param path O caminho para o arquivo de imagem (ex: "/spritesheet.png").
     */
	public Spritesheet(String path) {
		try {
			spritesheet = ImageIO.read(getClass().getResource(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public BufferedImage getSprite(int x, int y, int width, int height) {
		return spritesheet.getSubimage(x, y, width, height);
	}
}
