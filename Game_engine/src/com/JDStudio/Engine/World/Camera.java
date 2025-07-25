package com.JDStudio.Engine.World;


public class Camera {

    public static int x = 0;
    public static int y = 0;

    /**
     * Limita um valor para que ele permaneça dentro de um intervalo específico.
     * Se o valor atual for menor que o mínimo, retorna o mínimo.
     * Se for maior que o máximo, retorna o máximo.
     *
     * @param Atual O valor atual que será verificado.
     * @param Min   O valor mínimo permitido no intervalo.
     * @param Max   O valor máximo permitido no intervalo.
     * @return      O valor limitado dentro do intervalo [Min, Max].
     */
    public static int clamp(int Atual, int Min, int Max) {
        if (Atual < Min) {
            Atual = Min;
        }
        if (Atual > Max) {
            Atual = Max;
        }
        return Atual;
    }
}