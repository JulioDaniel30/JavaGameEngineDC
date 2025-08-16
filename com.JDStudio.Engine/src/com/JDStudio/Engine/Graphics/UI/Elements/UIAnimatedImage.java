package com.JDStudio.Engine.Graphics.UI.Elements;

import java.awt.Graphics;
import com.JDStudio.Engine.Graphics.Sprite.Sprite;
import com.JDStudio.Engine.Graphics.Sprite.Animations.Animation;
import com.JDStudio.Engine.Graphics.Sprite.Animations.Animator;

/**
 * Um elemento de UI que executa uma animação.
 * Ele encapsula um Animator para gerir e desenhar o frame atual de uma Animação.
 */
public class UIAnimatedImage extends UIElement {

    private Animator animator;

    /**
     * Cria um novo elemento de UI animado a partir de uma única animação.
     * @param x Posição X na tela.
     * @param y Posição Y na tela.
     * @param animation A animação a ser executada.
     */
    public UIAnimatedImage(int x, int y, Animation animation) {
        super(x, y);
        this.animator = new Animator();
        if (animation != null) {
            this.animator.addAnimation("default", animation);
            this.animator.play("default");
            
            Sprite frame = animation.getCurrentFrame();
            if (frame != null) {
                this.width = frame.getWidth();
                this.height = frame.getHeight();
            }
        }
    }

    @Override
    public void tick() {
        if (visible && animator != null) {
            animator.update();
        }
    }

    @Override
    public void render(Graphics g) {
        if (visible && animator != null) {
            Sprite currentFrame = animator.getCurrentSprite();
            if (currentFrame != null) {
                g.drawImage(currentFrame.getImage(), x, y, null);
            }
        }
    }
    
    /**
     * Permite o acesso ao Animator interno para um controlo mais avançado
     * (ex: adicionar e trocar entre múltiplas animações).
     * @return O Animator deste elemento.
     */
    public Animator getAnimator() {
        return this.animator;
    }
}