// engine
package com.JDStudio.Engine.Graphics.Sprite.Animations;

import com.JDStudio.Engine.Graphics.Sprite.Sprite;

public class Animation {

    private final Sprite[] frames;
    private final int animationSpeed;
    private final boolean loop; // <-- NOSSO NOVO "INTERRUPTOR"
    
    private int currentFrame = 0;
    private int frameCount = 0;
    private boolean finished = false; // <-- Flag para saber se a animação terminou

    /**
     * Construtor completo para criar uma animação.
     * @param speed  Velocidade da animação (ticks por frame).
     * @param loop   Se a animação deve repetir ao terminar.
     * @param frames Os sprites que compõem a animação.
     */
    public Animation(int speed, boolean loop, Sprite... frames) {
        this.animationSpeed = speed;
        this.loop = loop;
        this.frames = frames;
    }

    /**
     * Construtor de conveniência para animações em loop (comportamento antigo).
     */
    public Animation(int speed, Sprite... frames) {
        this(speed, true, frames); // Por padrão, cria uma animação em loop
    }

    public void tick() {
        if (finished) return; // Se já terminou, não faz mais nada

        frameCount++;
        if (frameCount >= animationSpeed) {
            frameCount = 0;
            currentFrame++;
            if (currentFrame >= frames.length) {
                if (loop) {
                    // Se for em loop, reinicia
                    currentFrame = 0;
                } else {
                    // Se não for em loop, para no último frame e marca como finalizada
                    currentFrame = frames.length - 1;
                    finished = true;
                }
            }
        }
    }
    
    public Sprite getCurrentFrame() {
        return frames[currentFrame];
    }
    
    /**
     * Reinicia a animação, permitindo que ela toque novamente.
     */
    public void reset() {
        this.currentFrame = 0;
        this.frameCount = 0;
        this.finished = false;
    }

    /**
     * Verifica se uma animação sem loop já terminou de tocar.
     * @return true se a animação tocou até o fim, false caso contrário.
     */
    public boolean hasFinished() {
        return this.finished;
    }
}