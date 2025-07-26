// Arquivo: Spritesheet.java
package com.JDStudio.Engine.Graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;

/**
 * Representa uma folha de sprites (spritesheet) carregada de um arquivo de imagem.
 * <p>
 * Esta classe serve como uma ferramenta para carregar uma única imagem grande
 * que contém múltiplos sprites e extrair sub-imagens individuais dela.
 *
 * @author JD Studio
 * @since 1.0
 */
public class Spritesheet {

    /** A imagem completa da folha de sprites. */
    private final BufferedImage sheet;

    /**
     * Carrega uma spritesheet a partir de um caminho no classpath.
     *
     * @param path O caminho para o arquivo de imagem (ex: "/textures/sheet.png"). Não pode ser nulo.
     * @throws RuntimeException se o caminho for nulo ou se houver um erro ao carregar a imagem.
     */
    public Spritesheet(String path) {
        Objects.requireNonNull(path, "O caminho para a spritesheet não pode ser nulo.");
        try {
            sheet = ImageIO.read(getClass().getResource(path));
            if (sheet == null) {
                throw new IOException("Arquivo de imagem não encontrado no caminho: " + path);
            }
        } catch (IOException e) {
            // Lança uma exceção não verificada para sinalizar uma falha crítica de inicialização.
            throw new RuntimeException("Falha ao carregar a spritesheet de: " + path, e);
        }
    }

    /**
     * Extrai um sprite individual da folha de sprites com base em coordenadas de pixel.
     *
     * @param x      A coordenada X do canto superior esquerdo do sprite na folha.
     * @param y      A coordenada Y do canto superior esquerdo do sprite na folha.
     * @param width  A largura em pixels do sprite a ser extraído.
     * @param height A altura em pixels do sprite a ser extraído.
     * @return Um novo objeto {@link Sprite} contendo a imagem recortada.
     */
    public Sprite getSprite(int x, int y, int width, int height) {
        BufferedImage subImage = sheet.getSubimage(x, y, width, height);
        return new Sprite(subImage);
    }

    /**
     * Retorna a largura total da folha de sprites em pixels.
     *
     * @return A largura da imagem.
     */
    public int getWidth() {
        return sheet.getWidth();
    }

    /**
     * Retorna a altura total da folha de sprites em pixels.
     *
     * @return A altura da imagem.
     */
    public int getHeight() {
        return sheet.getHeight();
    }
}