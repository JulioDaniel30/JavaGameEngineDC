package com.jdstudio.engine.Graphics.UI.Inventory;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import com.jdstudio.engine.Graphics.Sprite.Sprite;
import com.jdstudio.engine.Graphics.UI.Elements.UIElement;
import com.jdstudio.engine.Input.InputManager;
import com.jdstudio.engine.Items.Inventory;
import com.jdstudio.engine.Items.ItemStack;
import com.jdstudio.engine.Object.GameObject;

public class UIInventoryView extends UIElement {

	private final Inventory inventory;
    private final List<UIInventorySlot> slots;
    private final GameObject inventoryOwner;
    private Sprite backgroundSprite;
    private int rows, columns;
    private int slotSize, padding;
    private int selectedIndex = 0; // Começa com o primeiro slot selecionado

    public UIInventoryView(int x, int y, Sprite background, Sprite slotBackground, GameObject owner, Inventory inventory, int rows, int columns, int slotSize, int padding) {
        super(x, y);
        this.inventoryOwner = owner;
        this.inventory = inventory;
        this.rows = rows;
        this.columns = columns;
        this.slotSize = slotSize;
        this.padding = padding;
        this.slots = new ArrayList<>();

        if (background != null) { this.width = background.getWidth(); this.setHeight(background.getHeight()); }

        createSlots(slotBackground);
    }

    private void createSlots(Sprite slotBackground) {
        int totalSlotsToCreate = Math.min(inventory.getCapacity(), rows * columns);
        for (int i = 0; i < totalSlotsToCreate; i++) {
            int col = i % columns;
            int row = i / columns;
            int slotX = this.x + padding + col * (slotSize + padding);
            int slotY = this.y + padding + row * (slotSize + padding);
            slots.add(new UIInventorySlot(slotX, slotY, slotBackground, this.inventoryOwner));
        }
    }

    private void updateSlots() {
        List<ItemStack> items = inventory.getItems();
        for (int i = 0; i < slots.size(); i++) {
            if (i < items.size()) {
                slots.get(i).setItemStack(items.get(i));
            } else {
                slots.get(i).setItemStack(null);
            }
        }
    }

    @Override
    public void tick() {
        if (!visible) return;
        
        updateSlots();
        handleKeyboardInput();
        
        // Atualiza cada slot individual E verifica cliques do rato
        for (int i = 0; i < slots.size(); i++) {
            UIInventorySlot slot = slots.get(i);
            slot.setSelected(i == selectedIndex);
            slot.tick();
            
            // --- NOVA LÓGICA DE CLIQUE DO RATO ---
            // Se este slot foi clicado, usa o item nele
            if (slot.wasClicked()) {
                useItemInSlot(i);
            }
            // Se o rato está sobre este slot, atualiza o índice selecionado
            if (slot.isHovering()) {
                selectedIndex = i;
            }
        }
    }

    /**
     * Tenta usar o item no índice especificado.
     */
    private void useItemInSlot(int index) {
        if (index >= 0 && index < slots.size()) {
            ItemStack stack = slots.get(index).getItemStack();
            if (stack != null) {
                stack.getItem().onUse(inventoryOwner);
            }
        }
    }
    
    private void handleKeyboardInput() {
    	if (selectedIndex == -1) { // Se nada estiver selecionado, seleciona o primeiro slot
            selectedIndex = 0;
        }

        if (InputManager.isActionJustPressed("UI_DOWN")) {
            selectedIndex += columns; // Move para a linha de baixo
        }
        if (InputManager.isActionJustPressed("UI_UP")) {
            selectedIndex -= columns; // Move para a linha de cima
        }
        if (InputManager.isActionJustPressed("UI_RIGHT")) {
            selectedIndex++; // Move para a direita
        }
        if (InputManager.isActionJustPressed("UI_LEFT")) {
            selectedIndex--; // Move para a esquerda
        }

        // Garante que o índice não saia dos limites
        if (selectedIndex >= slots.size()) selectedIndex = selectedIndex % columns; // Volta para a primeira linha
        if (selectedIndex < 0) selectedIndex = slots.size() - 1; // Vai para o último slot

        // Lógica para USAR o item selecionado
        if (InputManager.isActionJustPressed("INTERACT")) {
            useItemInSlot(selectedIndex);
        }
    }

    @Override
    public void render(Graphics g) {
        if (!visible) return;

        // Desenha o fundo da janela do inventário
        if (backgroundSprite != null) {
            g.drawImage(backgroundSprite.getImage(), x, y, null);
        }
        
        // Desenha cada slot
        for (UIInventorySlot slot : slots) {
            slot.render(g);
        }
    }
}