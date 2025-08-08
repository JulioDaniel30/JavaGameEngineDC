package com.JDStudio.Engine.Components;

import com.JDStudio.Engine.Items.Inventory;
import com.JDStudio.Engine.Object.GameObject;

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