package com.venned.simplerealm.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.level.border.WorldBorder;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class CustomWorldBorder {
    private final net.minecraft.world.level.border.WorldBorder handle;

    public CustomWorldBorder(Player player) {
        this.handle = new WorldBorder();
        this.handle.world = ((CraftWorld)player.getWorld()).getHandle();
    }

    public void setCenter(Location location, Player player) {
        this.handle.c(location.getX(), location.getZ());
        sendPacketToPlayer(player, new ClientboundSetBorderCenterPacket(this.handle));
    }

    public void setSize(double size, Player player) {
        this.handle.a(size);
        sendPacketToPlayer(player, new ClientboundSetBorderSizePacket(this.handle));
    }

    public void setWarningDistance(int warningDistance, Player player) {
        this.handle.b(warningDistance);
        sendPacketToPlayer(player, new ClientboundSetBorderWarningDistancePacket(this.handle));
    }

    public void setWarningTime(int warningTime, Player player) {
        this.handle.c(warningTime);
        sendPacketToPlayer(player, new ClientboundSetBorderWarningDelayPacket(this.handle));
    }

    public void initialize(Player player) {
        sendPacketToPlayer(player, new ClientboundInitializeBorderPacket(this.handle));
    }

    private void sendPacketToPlayer(Player player, Packet<?> packet) {
        ServerPlayerConnection nmsPlayer = ((CraftPlayer)player).getHandle().c;
        nmsPlayer.b(packet);
    }
}

