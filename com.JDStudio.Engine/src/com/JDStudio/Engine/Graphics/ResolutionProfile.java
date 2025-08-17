package com.JDStudio.Engine.Graphics;

/**
 * Representa um perfil de resolução, contendo a largura e altura base
 * e uma escala recomendada para a janela.
 *
 * @param width A largura da resolução interna (em pixels).
 * @param height A altura da resolução interna (em pixels).
 * @param recommendedScale A escala de multiplicação sugerida para a janela final.
 */
public record ResolutionProfile(int width, int height, int recommendedScale) {
    
    /**
     * Calcula a largura final da janela.
     * @return A largura base multiplicada pela escala recomendada.
     */
    public int getFinalWidth() {
        return width * recommendedScale;
    }

    /**
     * Calcula a altura final da janela.
     * @return A altura base multiplicada pela escala recomendada.
     */
    public int getFinalHeight() {
        return height * recommendedScale;
    }
}