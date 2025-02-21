package com.chillguy.simplesky;

import com.venned.nms.BorderAPI;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.network.ServerPlayerConnection;
import org.bukkit.Location;
import net.minecraft.world.level.border.WorldBorder;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Border1_20_R3 implements BorderAPI {

    private final WorldBorder handle;

    public Border1_20_R3(Player player) {
        this.handle = new net.minecraft.world.level.border.WorldBorder();
        this.handle.world = ((CraftWorld)player.getWorld()).getHandle();
    }

    @Override
    public void setCenter(Location location, Player player) {
        this.handle.c(location.getX(), location.getZ());
        sendPacketToPlayer(player, new ClientboundSetBorderCenterPacket(this.handle));
    }

    @Override
    public void setSize(double size, Player player) {
        this.handle.a(size);
        sendPacketToPlayer(player, new ClientboundSetBorderSizePacket(this.handle));
    }

    @Override
    public void setWarningDistance(int warningDistance, Player player) {
        this.handle.b(warningDistance);
        sendPacketToPlayer(player, new ClientboundSetBorderWarningDistancePacket(this.handle));
    }

    @Override
    public void setWarningTime(int warningTime, Player player) {
        this.handle.c(warningTime);
        sendPacketToPlayer(player, new ClientboundSetBorderWarningDelayPacket(this.handle));
    }

    @Override
    public void initialize(Player player) {
        sendPacketToPlayer(player, new ClientboundInitializeBorderPacket(this.handle));
    }

    private void sendPacketToPlayer(Player player, Packet<?> packet) {
        ServerPlayerConnection nmsPlayer = ((CraftPlayer)player).getHandle().c;
        nmsPlayer.b(packet);
    }
}
