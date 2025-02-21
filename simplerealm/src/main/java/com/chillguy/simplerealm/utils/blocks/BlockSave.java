package com.chillguy.simplerealm.utils.blocks;

import org.bukkit.Material;

public class BlockSave {

    Material material;
    int quantity;

    public BlockSave(Material material, int quantity) {
        this.material = material;
        this.quantity = quantity;
    }

    public void decrementQuantity(){
        --quantity;
    }

    public void increaseQuantity(){
        quantity++;
    }

    public int getQuantity() {
        return quantity;
    }

    public Material getMaterial() {
        return material;
    }
}
