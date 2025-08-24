package com.jdstudio.engine.Graphics.UI;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import com.jdstudio.engine.Dialogue.DialogueChoice;
import com.jdstudio.engine.Dialogue.DialogueManager;
import com.jdstudio.engine.Dialogue.DialogueNode;
import com.jdstudio.engine.Graphics.UI.Elements.UIElement;
import com.jdstudio.engine.Input.InputManager;

/**
 * A UI element that displays dialogue text and choices to the player.
 * It includes features like a typewriter effect for text display and navigation
 * through dialogue choices.
 * 
 * @author JDStudio
 */
public class DialogueBox extends UIElement {

    private final DialogueManager dialogueManager;
    private int currentChoice = 0;
    private final int width, height;
    
    /** Flag indicating if input was consumed by the dialogue box in the current frame. */
    public boolean inputConsumedThisFrame = false;

    // Styling properties
    private Font nameFont = new Font("Arial", Font.BOLD, 14);
    private Font textFont = new Font("Arial", Font.PLAIN, 12);
    private Color boxColor = new Color(0, 0, 0, 220);
    private Color textColor = Color.WHITE;
    private Color selectedColor = Color.YELLOW;
    private Color borderColor = Color.WHITE;
    private int padding = 2;
    private int lineSpacing = 10;
    private int sectionSpacing = 10;
    private int moreChoiceSpacing = 2;
    private int moreSectionChoiceSpacion = 10;
    
    // Typewriter effect logic
    private String fullText = "";
    private String displayedText = "";
    private int charIndex = 0;
    private int timer = 0;
    private int typewriterSpeed = 2; // Ticks per character
    
    private String selectionCursor = "> ";
    
    /**
     * Sets the string used as a cursor for selected choices.
     * @param cursor The string to use as a selection cursor (e.g., "> ", "-> ").
     */
    public void setSelectionCursor(String cursor) {
        this.selectionCursor = cursor;
    }

    /**
     * Constructs a new DialogueBox.
     *
     * @param x      The x-coordinate of the top-left corner of the dialogue box.
     * @param y      The y-coordinate of the top-left corner of the dialogue box.
     * @param width  The width of the dialogue box.
     * @param height The height of the dialogue box.
     */
    public DialogueBox(int x, int y, int width, int height) {
        super(x, y);
        this.width = width;
        this.height = height;
        this.dialogueManager = DialogueManager.getInstance();
    }

    // --- Setters for Configuration ---

    /**
     * Sets the fonts for the speaker name and dialogue text.
     * @param nameFont The font for the speaker's name.
     * @param textFont The font for the dialogue text.
     */
    public void setFonts(Font nameFont, Font textFont) {
        this.nameFont = nameFont;
        this.textFont = textFont;
    }

    /**
     * Sets the colors for the box, text, selected choice, and border.
     * @param boxColor      The background color of the dialogue box.
     * @param textColor     The color of the dialogue text.
     * @param selectedColor The color of the selected choice.
     * @param borderColor   The color of the dialogue box border.
     */
    public void setColors(Color boxColor, Color textColor, Color selectedColor, Color borderColor) {
        this.boxColor = boxColor;
        this.textColor = textColor;
        this.selectedColor = selectedColor;
        this.borderColor = borderColor;
    }

    /**
     * Sets the padding around the text inside the dialogue box.
     * @param padding The padding in pixels.
     */
    public void setPadding(int padding) {
        this.padding = padding;
    }
    
    /**
     * Sets the spacing between lines of dialogue text.
     * @param lineSpacing The line spacing in pixels.
     */
    public void setLineSpacing(int lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    /**
     * Sets the spacing between the dialogue text section and the choices section.
     * @param sectionSpacing The section spacing in pixels.
     */
    public void setSectionSpacing(int sectionSpacing) {
        this.sectionSpacing = sectionSpacing;
    }

    /**
     * Sets the speed of the typewriter effect.
     * @param speed The number of ticks to wait before displaying the next character.
     */
    public void setTypewriterSpeed(int speed) {
        this.typewriterSpeed = Math.max(1, speed);
    }

    /**
     * Sets additional spacing between individual dialogue choices.
     * @param moreChoiceSpacing The additional spacing in pixels.
     */
    public void setMoreChoiceSpacing(int moreChoiceSpacing) {
        this.moreChoiceSpacing = moreChoiceSpacing;
    }

    /**
     * Sets additional spacing between the choices section and the bottom of the dialogue box.
     * @param moreSectionChoiceSpacion The additional spacing in pixels.
     */
    public void setMoreSectionChoiceSpacion(int moreSectionChoiceSpacion) {
    	this.moreSectionChoiceSpacion = moreSectionChoiceSpacion;
    }
    
    /**
     * Updates the dialogue box logic, including the typewriter effect and input handling.
     * This method should be called every game tick.
     */
    @Override
    public void tick() {
    	inputConsumedThisFrame = false; 
        if (!dialogueManager.isActive()) return;
        DialogueNode node = dialogueManager.getCurrentNode();
        if (node == null) return;

        // Reset typewriter if the node text has changed
        if (!node.text.equals(fullText)) {
            fullText = node.text;
            displayedText = "";
            charIndex = 0;
            timer = 0;
        }

        // Advance typewriter effect
        if (charIndex < fullText.length()) {
            timer++;
            if (timer >= typewriterSpeed) {
                timer = 0;
                charIndex++;
                displayedText = fullText.substring(0, charIndex);
            }
        }

        // Input handling
        if (InputManager.isActionJustPressed("INTERACT")) {
            if (charIndex < fullText.length()) {
                // If text is still appearing, fast-forward it
                charIndex = fullText.length();
                displayedText = fullText;
            } else {
                // If text has finished, select the current choice
                dialogueManager.selectChoice(currentChoice);
                currentChoice = 0; // Reset choice for the next node
            }
            inputConsumedThisFrame = true;
        }
        
        // Navigation through choices (only if text has finished and choices exist)
        List<DialogueChoice> choices = node.getChoices();
        if (!choices.isEmpty() && charIndex >= fullText.length()) {
            if (InputManager.isActionJustPressed("UI_DOWN")) {
                currentChoice = (currentChoice + 1) % choices.size();
            } else if (InputManager.isActionJustPressed("UI_UP")) {
                currentChoice--;
                if (currentChoice < 0) {
                    currentChoice = choices.size() - 1;
                }
            }
        }
    }
    
    /**
     * Renders the dialogue box, speaker name, dialogue text (with typewriter effect),
     * and interactive choices.
     *
     * @param g The Graphics context to draw on.
     */
    @Override
    public void render(Graphics g) {
        if (!dialogueManager.isActive()) return;
        
        DialogueNode node = dialogueManager.getCurrentNode();
        if (node == null) return;
        
        // Draw background box
        g.setColor(boxColor);
        g.fillRect(x, y, width, height);
        g.setColor(borderColor);
        g.drawRect(x, y, width, height);
        
        int currentY = y + padding;

        // Draw speaker name
        g.setFont(nameFont);
        g.setColor(textColor);
        currentY += g.getFontMetrics().getAscent();
        g.drawString(node.speakerName + ":", x + padding, currentY);

        currentY += sectionSpacing;

        // Draw dialogue text with word wrapping
        g.setFont(textFont);
        List<String> textLines = getWrappedLines(displayedText, g.getFontMetrics(), width - (padding * 2));

        for (String line : textLines) {
            g.drawString(line, x + padding, currentY);
            currentY += lineSpacing;
        }
        
        // Draw choices if text has finished and choices exist
        List<DialogueChoice> choices = node.getChoices();

        if (charIndex >= fullText.length() && !choices.isEmpty()) {
            FontMetrics metrics = g.getFontMetrics(textFont);
            int choiceSpacing = metrics.getHeight() + moreChoiceSpacing;
            
            // Calculate initial Y position for the FIRST choice
            int choiceY_start = y + height - (choices.size() * choiceSpacing) - padding + moreSectionChoiceSpacion;
            
            for (int i = 0; i < choices.size(); i++) {
                DialogueChoice choice = choices.get(i);
                String choiceText = choice.text;

                // Calculate Y position for the current choice
                int currentChoiceY = choiceY_start + (i * choiceSpacing);

                if (i == currentChoice) {
                    g.setColor(selectedColor);
                    choiceText = selectionCursor + choiceText; 
                } else {
                    g.setColor(textColor);
                    // Add blank spaces to align
                    String pad = " ".repeat(selectionCursor.length());
                    choiceText = pad + choiceText;
                }
                g.drawString(choiceText, x + padding, currentChoiceY);
            }
        }
    }
    
    /**
     * Wraps a given text into multiple lines to fit within a maximum width.
     *
     * @param text     The text to wrap.
     * @param metrics  The FontMetrics to measure string width.
     * @param maxWidth The maximum width for each line.
     * @return A List of strings, where each string is a wrapped line.
     */
    private List<String> getWrappedLines(String text, FontMetrics metrics, int maxWidth) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) return lines;

        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder(words[0]);

        for (int i = 1; i < words.length; i++) {
            if (metrics.stringWidth(currentLine + " " + words[i]) < maxWidth) {
                currentLine.append(" ").append(words[i]);
            } else {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(words[i]);
            }
        }
        lines.add(currentLine.toString());
        return lines;
    }
}