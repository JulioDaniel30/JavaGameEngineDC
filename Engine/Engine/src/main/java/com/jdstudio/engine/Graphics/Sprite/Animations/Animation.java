package com.jdstudio.engine.Graphics.Sprite.Animations;

import com.jdstudio.engine.Graphics.Sprite.Sprite;

/**
 * Represents a single animation sequence composed of a series of sprites.
 * It manages frame progression, animation speed, and looping behavior.
 * 
 * @author JDStudio
 */
public class Animation {

    /** The array of sprites that make up the animation frames. */
    private final Sprite[] frames;
    
    /** The speed of the animation, in game ticks per frame. */
    private final int animationSpeed;
    
    /** If true, the animation will loop back to the beginning when it finishes. */
    private final boolean loop;
    
    /** The index of the current frame being displayed. */
    private int currentFrame = 0;
    
    /** A counter to track ticks for animation speed. */
    private int frameCount = 0;
    
    /** A flag indicating if a non-looping animation has finished playing. */
    private boolean finished = false;

    /**
     * Full constructor to create an animation.
     *
     * @param speed  The speed of the animation (ticks per frame).
     * @param loop   If the animation should repeat when it finishes.
     * @param frames The sprites that compose the animation.
     */
    public Animation(int speed, boolean loop, Sprite... frames) {
        this.animationSpeed = speed;
        this.loop = loop;
        this.frames = frames;
    }

    /**
     * Convenience constructor for looping animations (old behavior).
     * Creates an animation that loops indefinitely.
     *
     * @param speed  The speed of the animation (ticks per frame).
     * @param frames The sprites that compose the animation.
     */
    public Animation(int speed, Sprite... frames) {
        this(speed, true, frames); // By default, creates a looping animation
    }

    /**
     * Updates the animation's state, advancing to the next frame if enough ticks have passed.
     * If the animation is non-looping and finishes, the `finished` flag is set.
     */
    public void tick() {
        if (finished) return; // If already finished, do nothing

        frameCount++;
        if (frameCount >= animationSpeed) {
            frameCount = 0;
            currentFrame++;
            if (currentFrame >= frames.length) {
                if (loop) {
                    // If looping, restart
                    currentFrame = 0;
                } else {
                    // If not looping, stop at the last frame and mark as finished
                    currentFrame = frames.length - 1;
                    finished = true;
                }
            }
        }
    }
    
    /**
     * Gets the current sprite frame of the animation.
     * @return The current Sprite to be rendered.
     */
    public Sprite getCurrentFrame() {
        return frames[currentFrame];
    }
    
    /**
     * Resets the animation to its initial state, allowing it to play again from the beginning.
     */
    public void reset() {
        this.currentFrame = 0;
        this.frameCount = 0;
        this.finished = false;
    }

    /**
     * Checks if a non-looping animation has finished playing.
     * @return true if the animation has played to its end, false otherwise.
     */
    public boolean hasFinished() {
        return this.finished;
    }
}
