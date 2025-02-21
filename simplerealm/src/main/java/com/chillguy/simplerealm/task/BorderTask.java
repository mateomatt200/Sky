package com.chillguy.simplerealm.task;

import com.chillguy.simplerealm.realm.Realm;
import com.chillguy.simplerealm.utils.CuboidUtils;
import com.chillguy.simplesky.Border1_20_R3;
import com.venned.nms.BorderAPI;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BorderTask extends BukkitRunnable {

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Realm realm = Realm.getRealmFromLocation(player.getLocation());
            if (realm != null) {

                int MIN_BORDER_SIZE = realm.getLevel().getWidth();


                CuboidUtils cuboidUtils = realm.getCuboid();

                // Obtener las coordenadas del cuboide
                double lowerX = cuboidUtils.getLowerX();
                double lowerZ = cuboidUtils.getLowerZ();
                double upperX = cuboidUtils.getUpperX();
                double upperZ = cuboidUtils.getUpperZ();

                Location playerLocation = player.getLocation();

                // Calcular el centro ideal del WorldBorder basado en el jugador
                double centerX = Math.max(lowerX + MIN_BORDER_SIZE / 2.0,
                        Math.min(upperX - MIN_BORDER_SIZE / 2.0, playerLocation.getX()));
                double centerZ = Math.max(lowerZ + MIN_BORDER_SIZE / 2.0,
                        Math.min(upperZ - MIN_BORDER_SIZE / 2.0, playerLocation.getZ()));

                // Calcular el tamaño del borde limitado por los límites del cuboide
                double maxSizeX = Math.min(upperX - centerX, centerX - lowerX) * 2;
                double maxSizeZ = Math.min(upperZ - centerZ, centerZ - lowerZ) * 2;
                double borderSize = Math.max(MIN_BORDER_SIZE, Math.min(maxSizeX, maxSizeZ));

                // Establecer el WorldBorder para el jugador
                Location borderCenter = new Location(player.getWorld(), centerX, playerLocation.getY(), centerZ);
                sendBorder(player, borderCenter, (int) borderSize);

                // Opcional: Spawn de partículas para indicar los límites del cuboide
              //  spawnBarrierParticles(cuboidUtils);
            }
        }
    }

    private void sendBorder(Player player, Location center, int border) {
        BorderAPI borderAPI = new Border1_20_R3(player);
        borderAPI.setCenter(center, player);
        borderAPI.setSize(border, player);
        borderAPI.setWarningDistance(5, player);
        borderAPI.setWarningTime(15, player);
        borderAPI.initialize(player);
    }

    private void spawnBarrierParticles(CuboidUtils cuboidUtils) {
        World world = cuboidUtils.getWorld();
        double lowerX = cuboidUtils.getLowerX();
        double lowerY = cuboidUtils.getLowerY();
        double lowerZ = cuboidUtils.getLowerZ();
        double upperX = cuboidUtils.getUpperX();
        double upperY = cuboidUtils.getUpperY();
        double upperZ = cuboidUtils.getUpperZ();

        // Crear las 4 paredes de la barrera (bloques de barrera)
        for (double x = lowerX; x <= upperX; x++) {
            for (double y = lowerY; y <= upperY; y++) {
                world.spawnParticle(Particle.BLOCK_MARKER, new Location(world, x, y, lowerZ), 1, Bukkit.createBlockData(Material.BARRIER));
                world.spawnParticle(Particle.BLOCK_MARKER, new Location(world, x, y, upperZ), 1, Bukkit.createBlockData(Material.BARRIER));
            }
        }
        for (double z = lowerZ; z <= upperZ; z++) {
            for (double y = lowerY; y <= upperY; y++) {
                world.spawnParticle(Particle.BLOCK_MARKER, new Location(world, lowerX, y, z), 1, Bukkit.createBlockData(Material.BARRIER));
                world.spawnParticle(Particle.BLOCK_MARKER, new Location(world, upperX, y, z), 1, Bukkit.createBlockData(Material.BARRIER));
            }
        }
    }
}