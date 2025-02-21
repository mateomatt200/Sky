package com.chillguy.simplerealm.utils;

import com.venned.nms.BorderAPI;
import com.chillguy.simplerealm.Main;
import com.chillguy.simplesky.Border1_20_R3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SendPlayerBorder {


    public static void setPlayer(Player player, Location center){

        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), ()-> {

            BorderAPI borderAPI = new Border1_20_R3(player);
            borderAPI.setCenter(center, player);

            borderAPI.setSize( 50, player);

            borderAPI.setWarningDistance(5, player);
            borderAPI.setWarningTime(15, player);

            borderAPI.initialize(player);
        }, 20L, 20L);

    }
}
