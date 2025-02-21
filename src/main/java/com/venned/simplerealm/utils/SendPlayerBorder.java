package com.venned.simplerealm.utils;

import com.venned.simplerealm.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SendPlayerBorder {


    public static void setPlayer(Player player, Location center){

        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), ()-> {

            CustomWorldBorder worldBorder = new CustomWorldBorder(player);


            worldBorder.setCenter(center, player);

            worldBorder.setSize( 10, player);

            worldBorder.setWarningDistance(5, player);
            worldBorder.setWarningTime(15, player);

            worldBorder.initialize(player);
        }, 20L, 20L);

    }
}
