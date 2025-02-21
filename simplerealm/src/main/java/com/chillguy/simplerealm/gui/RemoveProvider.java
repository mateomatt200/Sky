package com.chillguy.simplerealm.gui;
import com.chillguy.simplerealm.utils.TitleUtils;
import com.chillguy.simplerealm.realm.Realm;
import com.chillguy.simplerealm.utils.ItemsUtils;
import com.venned.simpleinventory.ClickableItem;
import com.venned.simpleinventory.content.InventoryContents;
import com.venned.simpleinventory.content.InventoryProvider;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;

public class RemoveProvider implements InventoryProvider {
    private Player player;
    private Realm realm;
    private ClickableItem yes,no,basic;

    public RemoveProvider(Player player, Realm realm) {
        this.player = player;
        this.realm = realm;
        setUpItems();
    }

    private void setUpItems() {
        yes = ClickableItem.of(new ItemsUtils(Material.GRAY_BANNER, "§cUnclaim Realm",(byte) 5, Arrays.asList("§7Unclaiming a realm will ", "§7remove your Realm and any ","§7progress on that Realm.")).toItemStack(), e -> {
            e.setCancelled(true);
            e.getWhoClicked().closeInventory();
            //new RealmUnclaimEvent(realm, RealmPlayer.getPlayer(player.getUniqueId().toString()),player);
            realm.delete();
            if( Bukkit.getVersion().contains("1.17") || Bukkit.getVersion().contains("1.18") || Bukkit.getVersion().contains("1.19") || Bukkit.getVersion().contains("1.20")){
                player.sendMessage("§e ----------------------------------§");
                player.sendMessage("           §bRealm unclaimed \n   §aClaim a new one with §6/claim");
                player.sendMessage("§e ----------------------------------§");
            }
            else{ TitleUtils.titlePacket(player, 20, 30, 20, "§bRealm unclaimed", "§aClaim a new one with §6/claim");
            }
        });

        no = ClickableItem.of(new ItemsUtils(Material.RED_BANNER, "§aKeep Realm",(byte) 14, Collections.singletonList("§7Cancel unclaim request.")).toItemStack(), e -> {
            e.setCancelled(true);
            e.getWhoClicked().closeInventory();
        });


        basic = ClickableItem.of(new ItemStack(Material.BLACK_STAINED_GLASS_PANE,1), e -> e.setCancelled(true));
    }


    @Override
    public void init(Player player, InventoryContents inventoryContents) {
        inventoryContents.fill(basic);
        inventoryContents.set(1,2,yes);
        inventoryContents.set(1,6,no);
    }



    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
