package com.venned.simplerealm.commands;

import com.venned.simplerealm.utils.SendPlayerBorder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Border implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player player) {
            SendPlayerBorder.setPlayer(player, player.getLocation());
        }
        return false;
    }
}
