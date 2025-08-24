package com.jdstudio.engine.Graphics.UI.Elements;

import java.awt.Graphics;
import com.jdstudio.engine.Graphics.Sprite.Sprite;
import com.jdstudio.engine.Graphics.Sprite.Animations.Animation;
import com.jdstudio.engine.Graphics.Sprite.Animations.Animator;

/**
 * A UI element that displays an animation.
 * It encapsulates an {@link Animator} to manage and draw the current frame of an {@link Animation}.
 * 
 * @author JDStudio
 */
public class UIAnimatedImage extends UIElement {

    /** The Animator instance managing the animations for this UI element. */
    private Animator animator;

    /**
     * Creates a new animated UI element from a single animation.
     *
     * @param x         The x-position on the screen.
     * @param y         The y-position on the screen.
     * @param animation The animation to be played.
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
                this.setHeight(frame.getHeight());
            }
        }
    }

    /**
     * Updates the internal animator, advancing the animation frames.
     * This method should be called every game tick.
     */
    @Override
    public void tick() {
        if (visible && animator != null) {
            animator.update();
        }
    }

    /**
     * Renders the current frame of the animation at the element's position.
     *
     * @param g The Graphics context to draw on.
     */
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
     * Allows access to the internal Animator for more advanced control
     * (e.g., adding and switching between multiple animations).
     * @return The Animator instance for this element.
     */
    public Animator getAnimator() {
        return this.animator;
    }
}
