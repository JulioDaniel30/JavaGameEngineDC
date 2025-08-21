package com.jdstudio.engine.Components;

import com.jdstudio.engine.Items.Inventory;
import com.jdstudio.engine.Object.GameObject;

public class InventoryComponent extends Component {
    
    public final Inventory inventory;

    public InventoryComponent(int capacity) {
        this.inventory = new Inventory(capacity);
    }

    @Override
    public void initialize(GameObject owner) {
        this.owner = owner;
    }
}