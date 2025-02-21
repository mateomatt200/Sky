package com.chillguy.simplerealm.gui;

import com.chillguy.simplerealm.Main;
import com.chillguy.simplerealm.realm.RealmConfig;
import com.chillguy.simplerealm.realm.RealmLevel;
import com.chillguy.simplerealm.realm.RealmPlayer;
import com.chillguy.simplerealm.realm.themes.ThemeType;
import com.chillguy.simplerealm.utils.ItemsUtils;
import com.chillguy.simplerealm.utils.TitleUtils;
import com.chillguy.simplerealm.realm.Realm;
import com.venned.simpleinventory.ClickableItem;
import com.venned.simpleinventory.content.InventoryContents;
import com.venned.simpleinventory.content.InventoryProvider;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PerkProvider implements InventoryProvider {
    private Player player;
    private RealmPlayer realmPlayer;
    private Realm realm;
    private ClickableItem grass, obsidian, basic, cancel, knight, lord, duke, skygod;

    public PerkProvider(Player player, RealmPlayer realmPlayer) {
        this.player = player;
        this.realmPlayer = realmPlayer;
        setUpItems();
    }

    private void setUpItems() {

        List<String> ranks = Main.getInstance().getRealmLevel().getPlayerRanks(player.getUniqueId());
        grass = createRealmItem("default", "Basic SkyRealm", Material.GRASS_BLOCK, ranks, "xp");
        obsidian = createRealmItem("hero", "Hero SkyRealm", Material.OBSIDIAN, ranks, "crops");
        knight = createRealmItem("knight", "Knight SkyRealm", Material.IRON_BLOCK, ranks, "crops");
        lord = createRealmItem("lord", "Lord SkyRealm", Material.GOLD_BLOCK, ranks, "crops");
        duke = createRealmItem("duke", "Duke SkyRealm", Material.DIAMOND_BLOCK, ranks, "crops");
        skygod = createRealmItem("skygod", "SkyGod SkyRealm", Material.EMERALD_BLOCK, ranks, "crops");


        cancel = ClickableItem.of(new ItemsUtils(Material.BARRIER, "§cCancel", (byte) 1, Arrays.asList("", "§7Cancel the creation of your realm")).toItemStack(), e -> {
            e.setCancelled(true);
            new WholeGUI().getPerkGui(player, realmPlayer).close(player);
        });

        basic = ClickableItem.of(new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1), e -> e.setCancelled(true));
    }

    private ClickableItem createRealmItem(String rank, String displayName, Material material, List<String> ranks, String perk) {
        RealmLevel realmLevel = Main.getInstance().getRealmLevel().getRealmByRankAndLevel(rank, 1);
        RealmLevel realmLevelMax = Main.getInstance().getRealmLevel().getRealmByRankAndLevel(rank, Main.getInstance().getRealmLevel().getMaxLevel(rank));

        boolean hasRank = ranks.contains(rank);
        String status = hasRank ? "§aAvailable" : "§cUnavailable";
        List<String> lore = new ArrayList<>(Arrays.asList(
                "§7Available to all Players.", "",
                "§7Starting Build Size: §e" + realmLevel.getBordersize() + "§7/§e" + realmLevel.getWidth(),
                "§7Max Build Size: §e" + realmLevelMax.getBordersize() + "§7/§e" + realmLevelMax.getWidth()
        ));

        if(!hasRank){
            lore.add("");
            lore.add("§cDont Have a Rank?");
            lore.add("§7Get one at §astore.skyrealms.games");
        }

        return ClickableItem.of(new ItemsUtils(material, "§b" + displayName + ": " + status, lore).toItemStack(), e -> {
            e.setCancelled(true);
            if(!hasRank){
                return;
            }
            new WholeGUI().getPerkGui(player, realmPlayer).close(player);
            createRealm(rank);
            realm.setPerk(perk);
            sendRealmClaimedMessage();
        });
    }

    private void sendRealmClaimedMessage() {
        if (Bukkit.getVersion().contains("1.17") || Bukkit.getVersion().contains("1.18")
                || Bukkit.getVersion().contains("1.19") || Bukkit.getVersion().contains("1.20")) {
            player.sendMessage("§e ----------------------------------§");
            player.sendMessage("           §bRealm claimed \n     §aGo to your Realm with §6/home");
            player.sendMessage("§e ----------------------------------§");
        } else {
            TitleUtils.titlePacket(player, 20, 30, 20, "§bRealm claimed", "§aGo to your Realm with §6/home");
        }
    }


    @Override
    public void init(Player player, InventoryContents inventoryContents) {
        inventoryContents.fill(basic);
        inventoryContents.set(0, 0, grass);
        inventoryContents.set(0, 1, obsidian);
        inventoryContents.set(0, 2, knight);
        inventoryContents.set(0, 3, lord);
        inventoryContents.set(0, 4, duke);
        inventoryContents.set(0, 5, skygod);
        inventoryContents.set(0, 8, cancel);

    }

    private void createRealm(String rank) {
        Realm realm = new Realm(realmPlayer, ThemeType.allthemeTypes.get(0), new RealmConfig().getNewLocation(), 1, 0, rank, Main.getInstance().getRealmMap().getGenLevels().get(0));
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
