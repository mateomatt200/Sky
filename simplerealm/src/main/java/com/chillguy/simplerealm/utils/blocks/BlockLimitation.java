package com.chillguy.simplerealm.utils.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockLimitation {

    Material block;
    int quantity;

    public BlockLimitation(Material block, int quantity) {
        this.block = block;
        this.quantity = quantity;
    }

    public Material getBlock() {
        return block;
    }

    public int getQuantity() {
        return quantity;
    }
}
