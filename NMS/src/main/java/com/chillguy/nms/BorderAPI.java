package com.venned.nms;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface BorderAPI {
    void setCenter(Location center, Player player);
    void setSize(double size, Player player);
    void setWarningDistance(int warningDistance, Player player);
    void setWarningTime(int warningTime, Player player);
    void initialize(Player player);
}
