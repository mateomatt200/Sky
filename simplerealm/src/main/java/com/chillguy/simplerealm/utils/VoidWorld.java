package com.chillguy.simplerealm.utils;


import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class VoidWorld {

    /**
     * Devuelve un generador de mundo vacío.
     */
    public static ChunkGenerator getDefaultWorldGenerator() {
        return new VoidChunkGenerator();
    }

    /**
     * Clase interna para generar chunks vacíos.
     */
    public static class VoidChunkGenerator extends ChunkGenerator {

        @Override
        public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
            // Crea un chunk vacío
            return createChunkData(world);
        }
    }
}
