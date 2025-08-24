package com.jdstudio.engine.Graphics.UI.Elements;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.function.Supplier;

/**
 * A UI element that renders text on the screen.
 * <p>
 * This class is capable of displaying both static text and dynamic text,
 * whose content can change every frame. Dynamicity is achieved through
 * a {@link Supplier<String>}, which provides the updated text.
 *
 * @author JDStudio
 * @since 1.0
 * @see UIElement
 */
public class UIText extends UIElement {

    /** The font used to render the text. */
    private Font font;

    /** The color of the text. */
    private Color color;

    /**
     * A functional supplier that returns the current text string.
     * Allows the text to be updated dynamically with each rendering.
     * For example, to display an FPS counter: {@code () -> "FPS: " + getFPS()}
     */
    private Supplier<String> textSupplier;

    /**
     * Constructs a new dynamic text UI element.
     *
     * @param x            The horizontal coordinate (X-axis) where the text will be drawn.
     * @param y            The vertical coordinate (Y-axis) where the text will be drawn.
     * @param font         The {@link Font} object to style the text.
     * @param color        The {@link Color} object to color the text.
     * @param textSupplier A {@code Supplier} function that returns the string to be displayed.
     *                     It is executed on each call to the render method.
     */
    public UIText(int x, int y, Font font, Color color, Supplier<String> textSupplier) {
        super(x, y);
        this.font = font;
        this.color = color;
        this.textSupplier = textSupplier;
    }

    /**
     * Constructs a new static text UI element.
     * <p>
     * This is a convenience constructor that internally creates a {@code Supplier}
     * for a text that never changes.
     *
     * @param x          The horizontal coordinate (X-axis).
     * @param y          The vertical coordinate (Y-axis).
     * @param font       The {@link Font} object to style the text.
     * @param color      The {@link Color} object to color the text.
     * @param staticText The fixed text string that will be displayed.
     */
    public UIText(int x, int y, Font font, Color color, String staticText) {
        // Reuses the main constructor, wrapping the static text in a Supplier.
        this(x, y, font, color, () -> staticText);
    }

    /**
     * Renders the text on the screen.
     * On each call, it obtains the latest value from the {@code textSupplier},
     * sets the font and color in the graphics context, and then draws the string
     * at the element's (x, y) position.
     *
     * @param g The Graphics context to draw on.
     */
    @Override
    public void render(Graphics g) {
        g.setColor(color);
        g.setFont(font);
        g.drawString(textSupplier.get(), x, y);
    }
}
