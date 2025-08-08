
// engine
package com.JDStudio.Engine.Graphics.UI;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import com.JDStudio.Engine.Dialogue.DialogueChoice;
import com.JDStudio.Engine.Dialogue.DialogueManager;
import com.JDStudio.Engine.Dialogue.DialogueNode;
import com.JDStudio.Engine.Graphics.UI.Elements.UIElement;
import com.JDStudio.Engine.Input.InputManager;

public class DialogueBox extends UIElement {

    private final DialogueManager dialogueManager;
    private int currentChoice = 0;
    private final int width, height;

    // Estilos
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
    
    // Lógica do efeito "máquina de escrever"
    private String fullText = "";
    private String displayedText = "";
    private int charIndex = 0;
    private int timer = 0;
    private int typewriterSpeed = 2;
    
    private String selectionCursor = "> ";
    
    public void setSelectionCursor(String cursor) {
        this.selectionCursor = cursor;
    }


    public DialogueBox(int x, int y, int width, int height) {
        super(x, y);
        this.width = width;
        this.height = height;
        this.dialogueManager = DialogueManager.getInstance();
    }

    // --- Setters para Configuração ---

    public void setFonts(Font nameFont, Font textFont) {
        this.nameFont = nameFont;
        this.textFont = textFont;
    }

    public void setColors(Color boxColor, Color textColor, Color selectedColor, Color borderColor) {
        this.boxColor = boxColor;
        this.textColor = textColor;
        this.selectedColor = selectedColor;
        this.borderColor = borderColor;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }
    
    public void setLineSpacing(int lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    public void setSectionSpacing(int sectionSpacing) {
        this.sectionSpacing = sectionSpacing;
    }

    public void setTypewriterSpeed(int speed) {
        this.typewriterSpeed = Math.max(1, speed);
    }
    public void setMoreChoiceSpacing(int moreChoiceSpacing) {
        this.moreChoiceSpacing = moreChoiceSpacing;
    }
    public void setMoreSectionChoiceSpacion(int moreSectionChoiceSpacion) {
    	this.moreSectionChoiceSpacion = moreSectionChoiceSpacion;
    }
    
    public void tick() {
        if (!dialogueManager.isActive()) return;
        DialogueNode node = dialogueManager.getCurrentNode();
        if (node == null) return;

        if (!node.text.equals(fullText)) {
            fullText = node.text;
            displayedText = "";
            charIndex = 0;
            timer = 0;
        }

        if (charIndex < fullText.length()) {
            timer++;
            if (timer >= typewriterSpeed) {
                timer = 0;
                charIndex++;
                displayedText = fullText.substring(0, charIndex);
            }
        }

        if (InputManager.isKeyJustPressed(KeyEvent.VK_ENTER) || InputManager.isKeyJustPressed(KeyEvent.VK_E)) {
            if (charIndex < fullText.length()) {
                charIndex = fullText.length();
                displayedText = fullText;
            } else {
                dialogueManager.selectChoice(currentChoice);
                currentChoice = 0;
            }
        }
        
        if (!node.choices.isEmpty() && charIndex >= fullText.length()) {
            if (InputManager.isKeyJustPressed(KeyEvent.VK_DOWN) || InputManager.isKeyJustPressed(KeyEvent.VK_S)) {
                currentChoice = (currentChoice + 1) % node.choices.size();
            } else if (InputManager.isKeyJustPressed(KeyEvent.VK_UP) || InputManager.isKeyJustPressed(KeyEvent.VK_W)) {
                currentChoice--;
                if (currentChoice < 0) currentChoice = node.choices.size() - 1;
            }
        }
    }
    
    @Override
    public void render(Graphics g) {
        if (!dialogueManager.isActive()) return;
        
        DialogueNode node = dialogueManager.getCurrentNode();
        if (node == null) return;
        
        g.setColor(boxColor);
        g.fillRect(x, y, width, height);
        g.setColor(borderColor);
        g.drawRect(x, y, width, height);
        
        int currentY = y + padding;

        g.setFont(nameFont);
        g.setColor(textColor);
        currentY += g.getFontMetrics().getAscent();
        g.drawString(node.speakerName + ":", x + padding, currentY);

        currentY += sectionSpacing;

        g.setFont(textFont);
        List<String> textLines = getWrappedLines(displayedText, g.getFontMetrics(), width - (padding * 2));

        for (String line : textLines) {
            g.drawString(line, x + padding, currentY);
            currentY += lineSpacing;
        }

        if (charIndex >= fullText.length() && !node.choices.isEmpty()) {
            FontMetrics metrics = g.getFontMetrics(textFont);
            int choiceSpacing = metrics.getHeight() + moreChoiceSpacing;
            
            // Calcula a posição Y inicial para a PRIMEIRA escolha, deixando espaço para todas as outras abaixo.
            int choiceY_start = y + height - (node.choices.size() * choiceSpacing) - padding + moreSectionChoiceSpacion;
            
            for (int i = 0; i < node.choices.size(); i++) {
                DialogueChoice choice = node.choices.get(i);
                String choiceText = choice.text;

                // Calcula a posição Y para a escolha atual
                int currentChoiceY = choiceY_start + (i * choiceSpacing);

                if (i == currentChoice) {
                    g.setColor(selectedColor);
                    choiceText = selectionCursor + choiceText; 
                } else {
                    g.setColor(textColor);
                    // Adiciona espaços em branco para alinhar com a opção selecionada
                    String pad = " ".repeat(selectionCursor.length());
                    choiceText = pad + choiceText;
                }
                g.drawString(choiceText, x + padding, currentChoiceY);
            }
        }
    }
    
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
