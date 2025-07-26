package com.JDStudio.Engine.Graphics.Sprite.Animations;

import com.JDStudio.Engine.Graphics.Sprite.Sprite;

/**
 * Representa uma sequência de sprites (frames) que formam uma animação.
 * Controla a velocidade da animação e qual frame deve ser exibido a cada momento.
 */
public class Animation {

    /** O array de sprites que compõem os quadros (frames) da animação. */
    private Sprite[] frames;
    
    /** A velocidade da animação, em "ticks por frame". Um valor maior resulta em uma animação mais lenta. */
    private int animationSpeed;
    
    /** O índice do quadro atual que está sendo exibido. */
    private int currentFrame = 0;
    
    /** Um contador de ticks para controlar o tempo até a troca para o próximo quadro. */
    private int frameCount = 0;

    /**
     * Cria uma nova instância de Animação.
     *
     * @param speed  A velocidade da animação, definida como o número de ticks do jogo
     * que cada frame deve durar. Quanto maior o valor, mais lenta a animação.
     * @param frames Uma sequência de objetos {@link Sprite} (varargs) que representam
     * os quadros da animação.
     */
    public Animation(int speed, Sprite... frames) {
        this.animationSpeed = speed;
        this.frames = frames;
    }

    /**
     * Atualiza a lógica da animação, avançando para o próximo frame se necessário.
     * Este método deve ser chamado a cada tick do jogo.
     */
    public void tick() {
        frameCount++;
        if (frameCount >= animationSpeed) {
            frameCount = 0;
            currentFrame++;
            if (currentFrame >= frames.length) {
                // Reinicia a animação quando chega ao fim (loop).
                currentFrame = 0;
            }
        }
    }

    /**
     * Obtém o sprite (frame) atual da animação que deve ser renderizado.
     * @return O {@link Sprite} correspondente ao quadro atual.
     */
    public Sprite getCurrentFrame() {
        return frames[currentFrame];
    }

    /**
     * Reinicia a animação para o primeiro frame.
     * Útil para quando uma ação específica precisa recomeçar sua animação (ex: um ataque).
     */
    public void reset() {
        this.currentFrame = 0;
        this.frameCount = 0;
    }
}