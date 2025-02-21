package com.chillguy.simplerealm.gui;


import com.chillguy.simplerealm.realm.themes.ThemeType;
import com.chillguy.simplerealm.realm.Realm;
import com.chillguy.simplerealm.utils.ItemsUtils;
import com.venned.simpleinventory.ClickableItem;
import com.venned.simpleinventory.content.InventoryContents;
import com.venned.simpleinventory.content.InventoryProvider;
import com.venned.simpleinventory.content.SlotIterator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class ThemeProvider implements InventoryProvider {

        private Player player;
        private Realm realm;
        private ArrayList<ThemeType> avaibletheme = new ArrayList<>();

        public ThemeProvider(Player player, Realm realm) {
            this.player = player;
            this.realm = realm;
            setAvaibleTheme();
        }

        public void setAvaibleTheme(){
            for(ThemeType t : ThemeType.allthemeTypes){
               if(player.hasPermission(t.getPermission()))
                   avaibletheme.add(t);
            }
        }
        @Override
        public void init(Player player, InventoryContents inventoryContents) {
            ClickableItem basic = ClickableItem.of(new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1, (byte) 15), e -> e.setCancelled(true));
            inventoryContents.fill(basic);
            for(ThemeType t : avaibletheme){
                setItem(inventoryContents,ClickableItem.of(t.getItem(),e ->{
                    realm.getTheme().setThemeType(t);
                    realm.getTheme().spawnTheme();
                    player.sendMessage("§aSuccessfully changed the type to: §e§l"+t.getName());
                    e.setCancelled(true);
                    player.closeInventory();
                }));

            }
            inventoryContents.set(2, 0, ClickableItem.of(new ItemsUtils(Material.BLACK_BED, "⬅ §bGo back", Arrays.asList("", "§7Click to go back to the", "§7Realm options.")).toItemStack(), e -> {
                e.setCancelled(true);
                player.closeInventory();
                new WholeGUI().openRealmGui(player, realm, false);
            }));
        }

        @Override
        public void update(Player player, InventoryContents inventoryContents) {

        }

        private void setItem(InventoryContents inventoryContents, ClickableItem clickableItem) {
            SlotIterator iterator = inventoryContents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0);
            while (!iterator.ended()) {
                if (iterator.get().isPresent()) {
                    if (iterator.get().get().getItem().getType() == Material.BLACK_STAINED_GLASS_PANE) {
                        inventoryContents.set(iterator.row(), iterator.column(), clickableItem);
                        return;
                    }
                }
                iterator.next();
            }

        }
    }
