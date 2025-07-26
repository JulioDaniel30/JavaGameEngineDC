package com.JDStudio.Engine.World;

/**
 * Uma classe estática que define a posição da câmera (viewport) no mundo do jogo.
 * <p>
 * Todos os objetos do jogo devem subtrair as coordenadas {@code Camera.x} e {@code Camera.y}
 * de suas próprias coordenadas no momento da renderização para criar o efeito de
 * uma câmera que se move pelo mundo. Esta classe não deve ser instanciada.
 *
 * @author JDStudio
 * @since 1.0
 */
public final class Camera {

    /** A coordenada X do canto superior esquerdo da câmera no mundo. */
    public static int x = 0;

    /** A coordenada Y do canto superior esquerdo da câmera no mundo. */
    public static int y = 0;

    /**
     * Construtor privado para impedir a instanciação desta classe utilitária.
     */
    private Camera() {}

    /**
     * Limita um valor para que ele permaneça dentro de um intervalo específico (mínimo e máximo).
     * <p>
     * Este método é útil para manter a câmera dentro dos limites do mapa do jogo,
     * impedindo que ela mostre áreas fora do mundo.
     *
     * @param atual O valor atual a ser limitado.
     * @param min   O valor mínimo permitido no intervalo.
     * @param max   O valor máximo permitido no intervalo.
     * @return      O valor limitado dentro do intervalo [min, max].
     */
    public static int clamp(int atual, int min, int max) {
        if (atual < min) {
            atual = min;
        }
        if (atual > max) {
            atual = max;
        }
        return atual;
    }
}