package com.jdstudio.engine.Components;

import com.jdstudio.engine.Items.Inventory;

/**
 * A component that gives a GameObject an inventory to store items.
 * It holds an instance of the Inventory class.
 * 
 * @author JDStudio
 */
public class InventoryComponent extends Component {
    
    /**
     * The inventory instance for this GameObject.
     */
    public final Inventory inventory;

    /**
     * Constructs a new InventoryComponent.
     *
     * @param capacity The maximum number of item stacks the inventory can hold.
     */
    public InventoryComponent(int capacity) {
        this.inventory = new Inventory(capacity);
    }
}
