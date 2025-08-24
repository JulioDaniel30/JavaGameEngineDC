package com.jdstudio.engine.Graphics.WSUI.PersonalityElements;

import java.awt.Graphics;
import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Graphics.Sprite.Sprite;
import com.jdstudio.engine.Graphics.WSUI.UIWorldAttached;
import com.jdstudio.engine.Object.GameObject;

/**
 * An emotion bubble that displays an icon (Sprite) above a {@link GameObject}
 * for a defined period of time. Ideal for NPC reactions (alert, idea, etc.).
 * 
 * @author JDStudio
 */
public class UIEmotionBubble extends UIWorldAttached {

    private Sprite emotionSprite;
    private int lifeTime; // Duration in ticks (frames)

    /**
     * Constructs a new UIEmotionBubble.
     *
     * @param target        The GameObject above which the bubble will appear.
     * @param emotionSprite The Sprite (icon) to be displayed.
     * @param lifeTime      The duration in ticks that the bubble will remain visible.
     */
    public UIEmotionBubble(GameObject target, Sprite emotionSprite, int lifeTime) {
        // The default Y offset can be adjusted to appear above the character's head
        super(target, -16); 
        this.emotionSprite = emotionSprite;
        this.lifeTime = lifeTime;

        if (emotionSprite != null) {
            this.width = emotionSprite.getWidth();
            this.height = emotionSprite.getHeight();
        }
    }

    /**
     * Updates the emotion bubble's logic each frame.
     * It decrements the lifetime, and when it reaches zero, the bubble becomes invisible
     * and is automatically removed from the rendering system.
     */
    @Override
    public void tick() {
        super.tick(); // The parent class (UIWorldAttached) already handles following the target.
        if (!visible) return;

        // Decrement lifetime
        lifeTime--;
        if (lifeTime <= 0) {
            // When time runs out, the bubble becomes invisible and will eventually be removed
            this.visible = false; 
            this.destroy(); // Use the destroy() method to remove itself from the RenderManager
        }
    }

    /**
     * Renders the emotion bubble's sprite.
     *
     * @param g The Graphics context to draw on.
     */
    @Override
    public void render(Graphics g) {
        if (!visible || target == null || emotionSprite == null) return;

        // The 'this.x' and 'this.y' coordinates are already updated by the parent's tick().
        // The calculation centers the sprite above the target.
        int drawX = (this.x - (this.width / 2)) - Engine.camera.getX();
        int drawY = this.y - this.getHeight() - Engine.camera.getY(); // Draw above the offset point

        g.drawImage(emotionSprite.getImage(), drawX, drawY, null);
    }
}
