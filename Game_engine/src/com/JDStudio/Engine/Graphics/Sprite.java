// Arquivo: Sprite.java
package com.JDStudio.Engine.Graphics;

import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * Representa uma imagem única (sprite) que pode ser desenhada na tela.
 * <p>
 * Esta classe atua como um wrapper para {@link BufferedImage}, permitindo futuras
 * extensões, como adicionar dados de animação ou metadados de colisão, sem
 * modificar todas as partes do motor que lidam com imagens.
 *
 * @author JDStudio
 * @since 1.0
 */
public class Sprite {

    /** A imagem rasterizada contendo os pixels do sprite. */
    private BufferedImage image;

    /**
     * Cria uma nova instância de Sprite a partir de um objeto BufferedImage.
     *
     * @param image A imagem a ser encapsulada por este sprite. Não pode ser nula.
     * @throws NullPointerException se a imagem fornecida for nula.
     */
    public Sprite(BufferedImage image) {
        // Validação para garantir que um sprite nunca seja criado sem uma imagem.
        this.image = Objects.requireNonNull(image, "A imagem de um sprite não pode ser nula.");
    }

    /**
     * Retorna o objeto {@link BufferedImage} subjacente para operações de renderização.
     *
     * @return A imagem contida neste sprite.
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Retorna a largura do sprite em pixels.
     *
     * @return A largura da imagem.
     */
    public int getWidth() {
        return image.getWidth();
    }

    /**
     * Retorna a altura do sprite em pixels.
     *
     * @return A altura da imagem.
     */
    public int getHeight() {
        return image.getHeight();
    }
}