// Arquivo: Spritesheet.java
package com.jdstudio.engine.Graphics.Sprite;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream; // Importe InputStream
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
     * Tenta carregar o recurso usando diferentes métodos para maior robustez.
     *
     * @param path O caminho para o arquivo de imagem (ex: "/textures/sheet.png"). Não pode ser nulo.
     * @throws RuntimeException se o caminho for nulo ou se houver um erro ao carregar a imagem.
     */
    public Spritesheet(String path) {
        Objects.requireNonNull(path, "O caminho para a spritesheet não pode ser nulo.");
        InputStream is = null;
        try {
            // Tenta obter o recurso usando o ClassLoader da própria classe Spritesheet
            is = getClass().getResourceAsStream(path);

            // Se a primeira tentativa falhar, tenta com o ClassLoader do sistema
            if (is == null) {
                System.out.println("DEBUG: Tentando carregar recurso via ClassLoader do sistema: " + path);
                is = ClassLoader.getSystemResourceAsStream(path);
            }

            // Se ainda falhar, tenta com o ClassLoader do Thread atual (pode ser útil em alguns frameworks)
            if (is == null) {
                 System.out.println("DEBUG: Tentando carregar recurso via Thread Context ClassLoader: " + path);
                 is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
            }

            // Se todas as tentativas falharem, o InputStream ainda será null
            if (is == null) {
                // Lança IOException para ser capturada pelo bloco catch abaixo
                throw new IOException("Arquivo de imagem não encontrado no classpath, mesmo após múltiplas tentativas: " + path);
            }

            sheet = ImageIO.read(is); // Usa o InputStream obtido

        } catch (IOException e) {
            // Lança uma exceção não verificada para sinalizar uma falha crítica de inicialização.
            throw new RuntimeException("Falha ao carregar a spritesheet de: " + path, e);
        } finally {
            if (is != null) {
                try {
                    is.close(); // Garante que o InputStream seja fechado
                } catch (IOException e) {
                    System.err.println("Erro ao fechar InputStream para: " + path + " - " + e.getMessage());
                }
            }
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
        // Certifique-se de que 'sheet' não é nulo antes de tentar subimage
        if (sheet == null) {
            throw new IllegalStateException("Spritesheet não foi carregada com sucesso.");
        }
        BufferedImage subImage = sheet.getSubimage(x, y, width, height);
        return new Sprite(subImage);
    }

    /**
     * Retorna a largura total da folha de sprites em pixels.
     *
     * @return A largura da imagem.
     */
    public int getWidth() {
        if (sheet == null) {
            return 0; // Ou lançar uma exceção, dependendo do comportamento desejado
        }
        return sheet.getWidth();
    }

    /**
     * Retorna a altura total da folha de sprites em pixels.
     *
     * @return A altura da imagem.
     */
    public int getHeight() {
        if (sheet == null) {
            return 0; // Ou lançar uma exceção
        }
        return sheet.getHeight();
    }
}