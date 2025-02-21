package com.chillguy.simplerealm.commands;


import com.chillguy.simplerealm.realm.RealmPlayer;
import com.chillguy.simplerealm.gui.WholeGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Unclaim implements CommandExecutor{
    public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args)
    {
        if (!(sender instanceof Player)) {
            sender.sendMessage("The command is only executable by a player !");

            return false;
        }
        if(cmd.getName().equalsIgnoreCase("unclaim")){
            Player player = (Player) sender;
            if(!player.hasPermission("realm.unclaim")){
                player.sendMessage("§cYou don't have the permission to do this.");
                return false;
            }
            RealmPlayer rp = RealmPlayer.getPlayer(player.getUniqueId().toString());
            if(rp.getOwned() != null){
                new WholeGUI().openUnclaimGui(player,rp.getOwned());
            }
            else
                player.sendMessage("§cYou don't have a realm to unclaim");
        }

        return true;
    }
}
