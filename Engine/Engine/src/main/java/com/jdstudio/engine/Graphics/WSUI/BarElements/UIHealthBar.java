package com.jdstudio.engine.Graphics.WSUI.BarElements;

import java.awt.Color;
import java.awt.Graphics;
import com.jdstudio.engine.Components.HealthComponent;
import com.jdstudio.engine.Graphics.WSUI.UIWorldAttached;
import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Object.GameObject;

/**
 * A world-attached UI element that displays the health of a {@link GameObject}.
 * It automatically retrieves the {@link HealthComponent} from its target and renders
 * a progress bar representing the current health percentage.
 * 
 * @author JDStudio
 */
public class UIHealthBar extends UIWorldAttached {

    private final HealthComponent healthComponent;
    private final int barWidth;
    private final int barHeight;
    private final Color backgroundColor;
    private final Color foregroundColor;

    /**
     * Constructs a new UIHealthBar.
     *
     * @param target  The GameObject whose health this bar will display.
     * @param yOffset The vertical offset from the target's Y position.
     * @param width   The width of the health bar.
     * @param height  The height of the health bar.
     */
    public UIHealthBar(GameObject target, int yOffset, int width, int height) {
        super(target, yOffset);
        this.healthComponent = target.getComponent(HealthComponent.class);
        this.barWidth = width;
        this.barHeight = height;
        this.backgroundColor = Color.DARK_GRAY;
        this.foregroundColor = Color.RED;

        // The bar is only visible if the target has a HealthComponent
        if (this.healthComponent == null) {
            this.visible = false;
            System.err.println("Warning: Attempted to create UIHealthBar for object without HealthComponent: " + target.name);
        }
    }

    /**
     * Renders the health bar, drawing its background, the filled foreground representing health,
     * and an optional border.
     *
     * @param g The Graphics context to draw on.
     */
    @Override
    public void render(Graphics g) {
        if (!visible || healthComponent == null) return;
        
        // Calculate rendering position on screen (considering camera)
        int drawX = (this.x - barWidth / 2) - Engine.camera.getX();
        int drawY = this.y - Engine.camera.getY();

        // 1. Draw the background of the bar
        g.setColor(backgroundColor);
        g.fillRect(drawX, drawY, barWidth, barHeight);

        // 2. Calculate and draw the fill (health)
        int fillWidth = (int) (barWidth * healthComponent.getHealthPercentage());
        g.setColor(foregroundColor);
        g.fillRect(drawX, drawY, fillWidth, barHeight);
        
        // 3. (Optional) Draw a border
        g.setColor(Color.BLACK);
        g.drawRect(drawX, drawY, barWidth, barHeight);
    }
}
