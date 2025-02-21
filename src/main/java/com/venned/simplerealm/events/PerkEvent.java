package com.venned.simplerealm.events;

import com.venned.simplerealm.realm.Realm;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class PerkEvent implements Listener {
    @EventHandler
    public void onCrops(BlockGrowEvent event){
        if(Realm.getRealmFromLocation(event.getBlock().getLocation()) != null){
            Realm realm = Realm.getRealmFromLocation(event.getBlock().getLocation());
            if(realm.getPerk() != null){
                if(realm.getPerk().equalsIgnoreCase("crops")){
                    event.getBlock().setBlockData(event.getBlock().getBlockData());
                }
            }
        }
    }
    @EventHandler
    public void onXp(PlayerExpChangeEvent event){
        if(Realm.getRealmFromLocation(event.getPlayer().getLocation()) != null){
            Realm realm = Realm.getRealmFromLocation(event.getPlayer().getLocation());
            if(realm.getPerk() != null){
                if(realm.getPerk().equalsIgnoreCase("xp")){
                    int expnew = (int)Math.round(event.getAmount() * 1.5);
                    event.setAmount(expnew);
                }
            }
        }
    }
}
