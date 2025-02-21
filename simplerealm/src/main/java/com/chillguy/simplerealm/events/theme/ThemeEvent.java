package com.chillguy.simplerealm.events.theme;

import com.chillguy.simplerealm.realm.Realm;
import com.chillguy.simplerealm.utils.CuboidUtils;
import com.chillguy.simplerealm.utils.blocks.BlockSave;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class ThemeEvent implements Listener {

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Location loc = event.getBlock().getLocation();

        if (Realm.getRealmFromLocation(loc) != null) {
            Realm realm = Realm.getRealmFromLocation(loc);

            CuboidUtils cuboidTheme = realm.getTheme().getCuboid().clone();
            CuboidUtils realmCuboid = realm.getCuboid().clone();

            cuboidTheme.expandToFit(realmCuboid);

            if(cuboidTheme.containsLocation(loc)){

                BlockSave blockSave = realm.getBlockSaves().stream().filter(c->c.getMaterial() == event.getBlock().getType()).findFirst().orElse(null);
                if(blockSave != null) {
                    blockSave.decrementQuantity();
                }


                event.setCancelled(true);
            }
        }

    }
}
