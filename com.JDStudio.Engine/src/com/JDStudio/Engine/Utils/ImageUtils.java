package com.JDStudio.Engine.Utils;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Uma classe de utilidades para operações comuns de manipulação de imagem.
 */
public class ImageUtils {

    /**
     * Inverte uma imagem horizontalmente.
     * @param image A imagem original.
     * @return Uma nova BufferedImage que é a versão invertida da original.
     */
    public static BufferedImage flipHorizontal(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        
        // Cria uma nova imagem com as mesmas dimensões e tipo
        BufferedImage flippedImage = new BufferedImage(width, height, image.getType());
        
        // Pega o contexto gráfico da nova imagem
        Graphics2D g2d = flippedImage.createGraphics();
        
        // Cria uma transformação para inverter no eixo X
        // 1. Escala em -1 no eixo X (inverte) e 1 no eixo Y (mantém)
        // 2. Translada a imagem de volta para a área visível, pois a escala negativa a joga para fora.
        AffineTransform transform = AffineTransform.getScaleInstance(-1, 1);
        transform.translate(-width, 0);
        
        // Aplica a transformação e desenha a imagem original na nova imagem
        g2d.setTransform(transform);
        g2d.drawImage(image, 0, 0, null);
        
        // Libera os recursos
        g2d.dispose();
        
        return flippedImage;
    }

    /**
     * Inverte uma imagem verticalmente.
     * @param image A imagem original.
     * @return Uma nova BufferedImage que é a versão invertida da original.
     */
    public static BufferedImage flipVertical(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage flippedImage = new BufferedImage(width, height, image.getType());
        Graphics2D g2d = flippedImage.createGraphics();
        
        AffineTransform transform = AffineTransform.getScaleInstance(1, -1);
        transform.translate(0, -height);
        
        g2d.setTransform(transform);
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        
        return flippedImage;
    }
}