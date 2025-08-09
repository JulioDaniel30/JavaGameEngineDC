package com.JDStudio.Engine.Graphics.UI.Inventory;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

import com.JDStudio.Engine.Engine;
import com.JDStudio.Engine.Graphics.Sprite.Sprite;
import com.JDStudio.Engine.Graphics.UI.Elements.UIElement;
import com.JDStudio.Engine.Input.InputManager;
import com.JDStudio.Engine.Items.ItemStack;
import com.JDStudio.Engine.Object.GameObject;

public class UIInventorySlot extends UIElement {

    private ItemStack itemStack;
    private Sprite backgroundSprite;
    private Font quantityFont = new Font("Arial", Font.BOLD, 10);
    private boolean isHovering = false;
    private boolean isSelected = false;
    private boolean wasClicked = false;
    private GameObject inventoryOwner;

    public UIInventorySlot(int x, int y, Sprite backgroundSprite, GameObject owner) {
        super(x, y);
        this.backgroundSprite = backgroundSprite;
        this.inventoryOwner = owner;
        if (backgroundSprite != null) {
            this.width = backgroundSprite.getWidth();
            this.height = backgroundSprite.getHeight();
        }
    }
    
    public void setItemStack(ItemStack stack) { this.itemStack = stack; }
    public ItemStack getItemStack() { return this.itemStack; }
    public void setSelected(boolean selected) { this.isSelected = selected; }
    public boolean wasClicked() { return this.wasClicked; }
    public boolean isHovering() { return this.isHovering; }
    
    @Override
    public void tick() {
        if (!visible) return;

        // Reseta o estado de clique a cada quadro
        wasClicked = false; 

        int mouseX = InputManager.getMouseX() / com.JDStudio.Engine.Engine.getSCALE();
        int mouseY = InputManager.getMouseY() / com.JDStudio.Engine.Engine.getSCALE();

        Rectangle bounds = new Rectangle(this.x, this.y, this.width, this.height);
        isHovering = bounds.contains(mouseX, mouseY);

        // Apenas deteta o clique e define uma flag. A UIInventoryView decidirá o que fazer.
        if (isHovering && InputManager.isLeftMouseButtonJustPressed() && itemStack != null) {
            wasClicked = true;
        }
    }

    @Override
    public void render(Graphics g) {
        if (!visible) return;

        // Desenha o fundo do slot
        if (backgroundSprite != null) {
            g.drawImage(backgroundSprite.getImage(), x, y, null);
        }

        // Se houver um item no slot, desenha-o
        if (itemStack != null && itemStack.getItem() != null) {
            Sprite itemSprite = itemStack.getItem().getSprite();
            if (itemSprite != null) {
                // Desenha o sprite do item centralizado dentro do slot
                int itemX = x + (width - itemSprite.getWidth()) / 2;
                int itemY = y + (height - itemSprite.getHeight()) / 2;
                g.drawImage(itemSprite.getImage(), itemX, itemY, null);
            }

            // Desenha a quantidade se for maior que 1
            if (itemStack.getQuantity() > 1) {
                g.setFont(quantityFont);
                g.setColor(Color.WHITE);
                String quantityText = String.valueOf(itemStack.getQuantity());
                int textWidth = g.getFontMetrics().stringWidth(quantityText);
                g.drawString(quantityText, x + width - textWidth - 2, y + height - 2);
            }
        }
        
     // --- LÓGICA DE DESTAQUE ATUALIZADA ---
        // Desenha um contorno se o rato estiver sobre o slot OU se ele estiver selecionado
        if (isHovering || isSelected) { // <-- MUDANÇA AQUI
            g.setColor(Color.YELLOW);
            g.drawRect(x, y, width - 1, height - 1);
        }
        
        // Adiciona um destaque extra se estiver selecionado
        if (isSelected) {
            g.setColor(new Color(255, 255, 0, 70)); // Amarelo semitransparente
            g.fillRect(x, y, width, height);
        }
    }
}