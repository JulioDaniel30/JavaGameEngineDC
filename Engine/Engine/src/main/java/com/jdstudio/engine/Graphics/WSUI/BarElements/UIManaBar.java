package com.jdstudio.engine.Graphics.WSUI.BarElements;

import java.awt.Color;
import java.awt.Graphics;
import com.jdstudio.engine.Components.ManaComponent;
import com.jdstudio.engine.Graphics.WSUI.UIWorldAttached;
import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Object.GameObject;

/**
 * A world-attached UI element that displays the mana of a {@link GameObject}.
 * It automatically retrieves the {@link ManaComponent} from its target and renders
 * a progress bar representing the current mana percentage.
 * 
 * @author Marcus Vinícius
 */
public class UIManaBar extends UIWorldAttached {

    private final ManaComponent manaComponent;
    private final int barWidth;
    private final int barHeight;
    private final Color backgroundColor;
    private final Color foregroundColor;

    /**
     * Constructs a new UIManaBar.
     *
     * @param target  The GameObject whose mana this bar will display.
     * @param yOffset The vertical offset from the target's Y position.
     * @param width   The width of the mana bar.
     * @param height  The height of the mana bar.
     */
    public UIManaBar(GameObject target, int yOffset, int width, int height) {
        super(target, yOffset);
        this.manaComponent = target.getComponent(ManaComponent.class);
        this.barWidth = width;
        this.barHeight = height;
        this.backgroundColor = Color.DARK_GRAY;
        this.foregroundColor = Color.BLUE;

        // The bar is only visible if the target has a ManaComponent
        if (this.manaComponent == null) {
            this.visible = false;
            System.err.println("Warning: Attempted to create UIManaBar for object without ManaComponent: " + target.name);
        }
    }

    /**
     * Renders the mana bar, drawing its background, the filled foreground representing mana,
     * and an optional border.
     *
     * @param g The Graphics context to draw on.
     */
    @Override
    public void render(Graphics g) {
        if (!visible || manaComponent == null) return;
        
        // Calculate rendering position on screen (considering camera)
        int drawX = (this.x - barWidth / 2) - Engine.camera.getX();
        int drawY = this.y - Engine.camera.getY();

        // 1. Draw the background of the bar
        g.setColor(backgroundColor);
        g.fillRect(drawX, drawY, barWidth, barHeight);

        // 2. Calculate and draw the fill (mana)
        int fillWidth = (int) (barWidth * manaComponent.getManaPercentage());
        g.setColor(foregroundColor);
        g.fillRect(drawX, drawY, fillWidth, barHeight);
        
        // 3. (Optional) Draw a border
        g.setColor(Color.BLACK);
        g.drawRect(drawX, drawY, barWidth, barHeight);
    }
}
