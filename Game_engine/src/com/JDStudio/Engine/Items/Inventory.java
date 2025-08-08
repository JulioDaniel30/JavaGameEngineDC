package com.JDStudio.Engine.Items;

import java.util.ArrayList;
import java.util.List;

public class Inventory {

    private final List<ItemStack> items;
    private final int capacity; // Número de slots do inventário

    public Inventory(int capacity) {
        this.capacity = capacity;
        this.items = new ArrayList<>(capacity);
    }

    /**
     * Tenta adicionar um item ao inventário.
     * @param itemToAdd O item a ser adicionado.
     * @param quantity A quantidade a ser adicionada.
     * @return A quantidade de itens que NÃO coube no inventário. Retorna 0 se tudo coube.
     */
    public int addItem(Item itemToAdd, int quantity) {
        int remainingQuantity = quantity;

        // 1. Tenta empilhar em stacks existentes
        for (ItemStack stack : items) {
            if (stack.getItem().id.equals(itemToAdd.id)) {
                int canAdd = stack.getItem().maxStackSize - stack.getQuantity();
                if (canAdd > 0) {
                    int amountToAdd = Math.min(remainingQuantity, canAdd);
                    stack.addQuantity(amountToAdd);
                    remainingQuantity -= amountToAdd;
                    if (remainingQuantity <= 0) return 0;
                }
            }
        }

        // 2. Se ainda sobrarem itens, tenta criar novos stacks em slots vazios
        while (remainingQuantity > 0 && items.size() < capacity) {
            int amountForNewStack = Math.min(remainingQuantity, itemToAdd.maxStackSize);
            items.add(new ItemStack(itemToAdd, amountForNewStack));
            remainingQuantity -= amountForNewStack;
        }

        return remainingQuantity; // Retorna o que sobrou
    }

    /**
     * Remove uma quantidade específica de um item do inventário, identificado pelo seu ID.
     * @param itemId O ID do item a ser removido (ex: "health_potion").
     * @param quantity A quantidade a ser removida.
     * @return true se a quantidade total foi removida com sucesso, false caso contrário.
     */
    public boolean removeItem(String itemId, int quantity) {
        int quantityToRemove = quantity;
        
        // Itera de trás para a frente para poder remover itens da lista com segurança
        for (int i = items.size() - 1; i >= 0; i--) {
            ItemStack stack = items.get(i);
            if (stack.getItem().id.equals(itemId)) {
                int amountInStack = stack.getQuantity();
                
                if (amountInStack >= quantityToRemove) {
                    stack.addQuantity(-quantityToRemove); // Diminui a quantidade
                    quantityToRemove = 0;
                } else {
                    quantityToRemove -= amountInStack;
                    stack.setQuantity(0); // Zera a quantidade deste stack
                }

                // Se o stack ficou vazio, remove-o do inventário
                if (stack.getQuantity() <= 0) {
                    items.remove(i);
                }

                if (quantityToRemove <= 0) {
                    return true; // Conseguiu remover tudo
                }
            }
        }
        return false; // Não conseguiu remover a quantidade pedida
    }
    
    public List<ItemStack> getItems() {
        return items;
    }

    public int getCapacity() {
        return capacity;
    }
}