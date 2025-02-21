package com.venned.simplerealm.gui;

import com.venned.simplerealm.realm.Realm;
import com.venned.simplerealm.realm.RealmConfig;
import com.venned.simplerealm.realm.RealmPlayer;
import com.venned.simplerealm.realm.themes.ThemeType;
import com.venned.simplerealm.utils.ItemsUtils;
import com.venned.simplerealm.utils.TitleUtils;
import com.venned.simpleinventory.ClickableItem;
import com.venned.simpleinventory.content.InventoryContents;
import com.venned.simpleinventory.content.InventoryProvider;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class PerkProvider implements InventoryProvider {
    private Player player;
    private RealmPlayer realmPlayer;
    private Realm realm;
    private ClickableItem xp, crops, basic, cancel;

    public PerkProvider(Player player, RealmPlayer realmPlayer) {
        this.player = player;
        this.realmPlayer = realmPlayer;
        setUpItems();
    }

    private void setUpItems() {
        xp = ClickableItem.of(new ItemsUtils(Material.EXPERIENCE_BOTTLE, "§bXP multiplicator", Arrays.asList("", "§7Get a 1.5x XP Booster In Your Realm")).toItemStack(), e -> {
            e.setCancelled(true);
            new WholeGUI().getPerkGui(player, realmPlayer).close(player);
            createRealm();
            realm.setPerk("xp");
            if( Bukkit.getVersion().contains("1.17") || Bukkit.getVersion().contains("1.18") || Bukkit.getVersion().contains("1.19") || Bukkit.getVersion().contains("1.20")){
                player.sendMessage("§e ----------------------------------§");
                player.sendMessage("           §bRealm claimed \n     §aGo to your Realm with §6/home");
                player.sendMessage("§e ----------------------------------§");
            }
            else{ TitleUtils.titlePacket(player, 20, 30, 20, "§bRealm claimed", "§aGo to your Realm with §6/home");
            }
        });

        crops = ClickableItem.of(new ItemsUtils(Material.WHEAT, "§bBetter Crops", Arrays.asList("", "§7Crops grow faster.")).toItemStack(), e -> {
            e.setCancelled(true);
            new WholeGUI().getPerkGui(player, realmPlayer).close(player);
            createRealm();
            realm.setPerk("crops");
            if( Bukkit.getVersion().contains("1.17") || Bukkit.getVersion().contains("1.18") || Bukkit.getVersion().contains("1.19") || Bukkit.getVersion().contains("1.20")){
                player.sendMessage("§e ----------------------------------§");
                player.sendMessage("           §bRealm claimed \n     §aGo to your Realm with §6/home");
                player.sendMessage("§e ----------------------------------§");
            }
            else{ TitleUtils.titlePacket(player, 20, 30, 20, "§bRealm claimed", "§aGo to your Realm with §6/home");
            }
        });
        cancel = ClickableItem.of(new ItemsUtils(Material.INK_SAC, "§cCancel", (byte) 1, Arrays.asList("", "§7Cancel the creation of your realm")).toItemStack(), e -> {
            e.setCancelled(true);
            new WholeGUI().getPerkGui(player, realmPlayer).close(player);
        });

        basic = ClickableItem.of(new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1), e -> e.setCancelled(true));
    }


    @Override
    public void init(Player player, InventoryContents inventoryContents) {
        inventoryContents.fill(basic);
        inventoryContents.set(0, 2, xp);
        inventoryContents.set(0, 6, crops);
        inventoryContents.set(0, 8, cancel);
    }

    private void createRealm() {
        Realm realm = new Realm(realmPlayer, ThemeType.allthemeTypes.get(0), new RealmConfig().getNewLocation(), 1, 0);
        realm.pasteIsland();
        double i = System.currentTimeMillis();
        System.out.println("Starting to fill the chest");
        realm.fillChest();
        double b = System.currentTimeMillis() - i;
        System.out.println("Time elapsed:" + b);
        new RealmConfig().updateRealm(realmPlayer.getOwned());
        this.realm = realm;//new RealmClaimEvent(realm,realmPlayer);
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }

}
