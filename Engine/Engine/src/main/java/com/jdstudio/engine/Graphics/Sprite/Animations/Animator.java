package com.jdstudio.engine.Graphics.Sprite.Animations;

import java.util.HashMap;
import java.util.Map;

import com.jdstudio.engine.Components.Component;
import com.jdstudio.engine.Graphics.Sprite.Sprite;

/**
 * A component that manages a set of animations for a GameObject.
 * It acts as a state machine for animations, controlling which one is active
 * and updating its logic.
 * 
 * @author JDStudio
 */
public class Animator extends Component{

    /** Map storing all available animations, associated with a text key. */
    private Map<String, Animation> animations;
    
    /** The text key of the animation that is currently playing. */
    private String currentAnimationKey;
    
    /** Direct reference to the active animation instance for quick access. */
    private Animation currentAnimation;

    /**
     * Creates a new Animator instance, initializing the animations map.
     */
    public Animator() {
        this.animations = new HashMap<>();
    }

    /**
     * Adds a new animation to the manager.
     * <p>
     * If this is the first animation added, it will be set as the active animation by default.
     *
     * @param key       The name of the animation (e.g., "walk_right", "idle").
     * @param animation The {@link Animation} object.
     */
    public void addAnimation(String key, Animation animation) {
        animations.put(key, animation);
        if (currentAnimation == null) {
            play(key);
        }
    }

    /**
     * Sets and starts an animation based on its key.
     * <p>
     * The animation is reset to the first frame. If the requested animation
     * is already playing, nothing happens to avoid unnecessary restarts.
     *
     * @param key The name of the animation to play.
     */
    public void play(String key) {
        // Optimization: Avoid restarting the animation if it's already playing.
        if (key.equals(currentAnimationKey)) {
            return;
        }

        if (animations.containsKey(key)) {
            this.currentAnimationKey = key;
            this.currentAnimation = animations.get(key);
            this.currentAnimation.reset();
        } else {
            System.err.println("Error: Animation '" + key + "' not found in Animator.");
        }
    }

    /**
     * Updates the active animation's logic, advancing its frame if necessary.
     * Should be called every game tick.
     */
    @Override
    public void update() {
        if (currentAnimation != null) {
            currentAnimation.tick();
        }
    }

    /**
     * Gets the current sprite (frame) of the active animation.
     *
     * @return The {@link Sprite} to be rendered, or {@code null} if no animation is active.
     */
    public Sprite getCurrentSprite() {
        if (currentAnimation != null) {
            return currentAnimation.getCurrentFrame();
        }
        return null;
    }
    
    /**
     * Returns the key of the animation that is currently playing.
     * Useful for checking the current state of the Animator.
     *
     * @return The key of the active animation, or {@code null} if none is active.
     */
    public String getCurrentAnimationKey() {
        return this.currentAnimationKey;
    }

    /**
     * Returns the current Animation object that is playing.
     * @return The active Animation object, or {@code null} if none is active.
     */
    public Animation getCurrentAnimation() {
        return this.currentAnimation;
    }
}
