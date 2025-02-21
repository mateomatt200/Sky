package com.chillguy.simplerealm.realm.gen;

import org.bukkit.Material;

public class GenChances {

    Material material;
    int chance;

    public GenChances(Material material, int chance) {
        this.material = material;
        this.chance = chance;
    }

    public Material getMaterial() {
        return material;
    }

    public int getChance() {
        return chance;
    }
}
