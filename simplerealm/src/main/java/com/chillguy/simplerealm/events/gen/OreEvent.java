package com.chillguy.simplerealm.events.gen;

import com.chillguy.simplerealm.realm.gen.GenChances;
import com.chillguy.simplerealm.realm.Realm;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;

import java.util.List;

public class OreEvent implements Listener {

    @EventHandler
    public void onBlockForm(BlockFormEvent event) {
        // Bloque que se está formando
        Block formedBlock = event.getBlock();
        Material formedType = event.getNewState().getType();
        if (Realm.getRealmFromLocation(event.getBlock().getLocation()) != null) {

            Realm realm = Realm.getRealmFromLocation(event.getBlock().getLocation());
            assert realm != null;
            // Verifica si el bloque formado es piedra (STONE)
            if (formedType == Material.COBBLESTONE) {
                // Obtén bloques adyacentes para verificar lava y agua
                boolean hasLava = false;
                boolean hasWater = false;

                for (Block relative : getSurroundingBlocks(formedBlock)) {
                    if (relative.getType() == Material.LAVA || relative.getType() == Material.LAVA_BUCKET) {
                        hasLava = true;
                    }
                    if (relative.getType() == Material.WATER || relative.getType() == Material.WATER_BUCKET) {
                        hasWater = true;
                    }
                }

                // Si hay lava y agua, fue una interacción natural
                if (hasLava && hasWater) {
                    event.setCancelled(true);
                    List<GenChances> genChancesList = realm.getGenLevel().getListBlocks();
                    Material chosenMaterial = getRandomMaterialByChance(genChancesList);
                    formedBlock.setType(chosenMaterial);
                }
            }
        }

    }

    private Material getRandomMaterialByChance(List<GenChances> genChancesList) {
        int totalWeight = 0;

        // Calcula el peso total
        for (GenChances chance : genChancesList) {
            totalWeight += chance.getChance();
        }

        // Genera un número aleatorio entre 0 y el peso total
        int random = (int) (Math.random() * totalWeight);

        // Recorre la lista y selecciona el bloque basado en el peso acumulado
        int currentWeight = 0;
        for (GenChances chance : genChancesList) {
            currentWeight += chance.getChance();
            if (random < currentWeight) {
                return chance.getMaterial();
            }
        }

        // Por si algo falla, devuelve un bloque por defecto
        return Material.STONE;
    }

    // Método para obtener los bloques adyacentes a un bloque
    private Block[] getSurroundingBlocks(Block block) {
        return new Block[]{
                block.getRelative(1, 0, 0),  // +X
                block.getRelative(-1, 0, 0), // -X
                block.getRelative(0, 1, 0),  // +Y
                block.getRelative(0, -1, 0), // -Y
                block.getRelative(0, 0, 1),  // +Z
                block.getRelative(0, 0, -1)  // -Z
        };
    }
}
