package com.chillguy.simplerealm.commands;

import com.chillguy.simplerealm.utils.SendPlayerBorder;
import com.chillguy.simplerealm.realm.Realm;
import com.chillguy.simplerealm.realm.RealmPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Border implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player player) {
            RealmPlayer rp = RealmPlayer.getPlayer(player.getUniqueId().toString());
            Realm realm = rp.getAllRealm().stream().findFirst().orElse(null);
            if(realm != null) {

                SendPlayerBorder.setPlayer(player, realm.getCenter());
            }
        }
        return false;
    }
}
