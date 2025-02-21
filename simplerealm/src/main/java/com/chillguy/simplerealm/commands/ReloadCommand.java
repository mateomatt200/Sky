package com.chillguy.simplerealm.commands;

import com.chillguy.simplerealm.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Main.getInstance().reloadServer();
        commandSender.sendMessage("Reloaded config");
        return true;
    }
}
