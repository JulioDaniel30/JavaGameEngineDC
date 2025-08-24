package com.jdstudio.engine.Graphics.WSUI.BarElements;

import java.awt.Color;
import java.awt.Graphics;
import com.jdstudio.engine.Components.ShieldComponent;
import com.jdstudio.engine.Graphics.WSUI.UIWorldAttached;
import com.jdstudio.engine.Engine;
import com.jdstudio.engine.Object.GameObject;

/**
 * A world-attached UI element that displays the shield health of a {@link GameObject}.
 * It automatically retrieves the {@link ShieldComponent} from its target and renders
 * a progress bar representing the current shield percentage.
 * 
 * @author JDStudio
 */
public class UIShieldBar extends UIWorldAttached {

    private final ShieldComponent shieldComponent;
    private int barWidth;
    private int barHeight;
    private Color backgroundColor;
    private Color foregroundColor;
    private boolean borderVisible; // Flag to control border visibility
    private Color borderColor; // Default border color

    /**
     * Constructs a new UIShieldBar.
     *
     * @param target  The GameObject whose shield this bar will display.
     * @param yOffset The vertical offset from the target's Y position.
     * @param width   The width of the shield bar.
     * @param height  The height of the shield bar.
     */
    public UIShieldBar(GameObject target, int yOffset, int width, int height) {
        super(target, yOffset);
        this.shieldComponent = target.getComponent(ShieldComponent.class);
        this.barWidth = width;
        this.barHeight = height;
        this.backgroundColor = Color.DARK_GRAY;
        this.foregroundColor = Color.BLUE; // Shield color
        this.visible = true; // Bar is visible by default
        this.borderVisible = true; // Border is visible by default
        this.borderColor = Color.BLACK; // Default border color

        // The bar is only visible if the target has a ShieldComponent
        if (this.shieldComponent == null) {
            this.visible = false;
            System.err.println("Warning: Attempted to create UIShieldBar for object without ShieldComponent: " + target.name);
        }
    }

    /**
     * Renders the shield bar, drawing its background, the filled foreground representing shield health,
     * and a conditional border.
     *
     * @param g The Graphics context to draw on.
     */
    @Override
    public void render(Graphics g) {
        if (!visible || shieldComponent == null) return;

        // Calculate rendering position on screen (considering camera)
        int drawX = (this.x - barWidth / 2) - Engine.camera.getX();
        int drawY = this.y - Engine.camera.getY();

        // 1. Draw the background of the bar
        g.setColor(backgroundColor);
        g.fillRect(drawX, drawY, barWidth, barHeight);

        // 2. Calculate and draw the fill (shield)
        int fillWidth = (int) (barWidth * (shieldComponent.getShieldHealth() / shieldComponent.getMaxShieldHealth()));
        g.setColor(foregroundColor);
        g.fillRect(drawX, drawY, fillWidth, barHeight);

        // 3. (Optional) Draw a border
        if (borderVisible) {
            g.setColor(borderColor);
            g.drawRect(drawX, drawY, barWidth, barHeight);
        }
    }

    /**
     * Sets the visibility of the bar's border.
     * @param visible true to show the border, false to hide it.
     */
    public void setBorderVisible(boolean visible) {
        this.borderVisible = visible;
    }

    /**
     * Checks if the bar's border is visible.
     * @return true if the border is visible, false otherwise.
     */
    public boolean isBorderVisible() {
        return this.borderVisible;
    }

    /**
     * Sets the color of the bar's border.
     * @param color The new border Color.
     */
    public void setBorderColor(Color color) {
        this.borderColor = color;
    }

    /**
     * Gets the color of the bar's border.
     * @return The border Color.
     */
    public Color getBorderColor() {
        return this.borderColor;
    }

    /**
     * Sets the dimensions (width and height) of the bar.
     * @param width The new width of the bar.
     * @param height The new height of the bar.
     */
    public void setBarDimensions(int width, int height) {
        this.barWidth = width;
        this.barHeight = height;
    }

    /**
     * Gets the width of the bar.
     * @return The width of the bar.
     */
    public int getBarWidth() {
        return barWidth;
    }

    /**
     * Gets the height of the bar.
     * @return The height of the bar.
     */
    public int getBarHeight() {
        return barHeight;
    }

    /**
     * Sets the position of the bar.
     * @param x The new x-coordinate.
     * @param y The new y-coordinate.
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the x-coordinate of the bar.
     * @return The x-coordinate.
     */
    public int getX() {
        return this.x;
    }

    /**
     * Gets the y-coordinate of the bar.
     * @return The y-coordinate.
     */
    public int getY() {
        return this.y;
    }

    /**
     * Checks if the bar is currently visible.
     * @return true if visible, false otherwise.
     */
    public boolean isVisible() {
        return this.visible;
    }

    /**
     * Sets the visibility of the bar.
     * @param visible true to make visible, false to hide.
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Gets the associated ShieldComponent.
     * @return The ShieldComponent instance.
     */
    public ShieldComponent getShieldComponent() {
        return this.shieldComponent;
    }

    /**
     * Sets the background color of the bar.
     * @param color The new background Color.
     */
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }

    /**
     * Gets the background color of the bar.
     * @return The background Color.
     */
    public Color getBackgroundColor() {
        return this.backgroundColor;
    }

    /**
     * Sets the foreground color of the bar.
     * @param color The new foreground Color.
     */
    public void setForegroundColor(Color color) {
        this.foregroundColor = color;
    }

    /**
     * Gets the foreground color of the bar.
     * @return The foreground Color.
     */
    public Color getForegroundColor() {
        return this.foregroundColor;
    }

    /**
     * Gets the recharge progress of the shield (from 0.0 to 1.0).
     * @return The recharge progress, or 0 if no ShieldComponent or ChargeableComponent is found.
     */
    public float getRechargeProgress() {
        if (shieldComponent == null || shieldComponent.getChargeableComponent() == null) {
            return 0;
        }
        return shieldComponent.getChargeableComponent().getProgress();
    }
}
