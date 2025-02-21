package com.venned.simplerealm.gui;

import com.venned.simplerealm.Main;
import com.venned.simplerealm.realm.Realm;
import com.venned.simplerealm.realm.RealmLevel;
import com.venned.simplerealm.realm.RealmPlayer;
import com.venned.simplerealm.realm.RealmRank;
import com.venned.simplerealm.utils.Config;
import com.venned.simplerealm.utils.ItemsUtils;
import com.venned.simpleinventory.ClickableItem;
import com.venned.simpleinventory.content.InventoryContents;
import com.venned.simpleinventory.content.InventoryProvider;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;

public class RealmProvider implements InventoryProvider {
    private Player player;
    private Realm realm;
    private ClickableItem banned, teleport, theme, upgrade, members, privacy, basic, back;
    private boolean from;
    private YamlConfiguration config;

    public RealmProvider(Player player, Realm realms, boolean from) {
        this.player = player;
        this.realm = realms;
        this.from = from;
        this.config = Config.ASPECT.getConfig();
        setUpItems();
    }

    public void setUpItems() {
        teleport = ClickableItem.of(new ItemsUtils(Config.getMaterial(config.getString("gui.realmgui.home.item")), Config.getStringWithReplacementRealm(config.getString("gui.realmgui.home.name"),realm), (byte) config.getInt("gui.realmgui.home.data"),Config.getListWithReplacementRealm(config.getStringList("gui.realmgui.home.lore"),realm)).toItemStack(), e -> {
            e.setCancelled(true);
            realm.teleportToSpawn(player);
            player.sendMessage(Config.getStringWithReplacementRealm(config.getString("gui.realmgui.home.clickmessage"),realm));
            e.getWhoClicked().closeInventory();
        });
        banned = ClickableItem.of(new ItemsUtils(Config.getMaterial(config.getString("gui.realmgui.banned.item")), Config.getStringWithReplacementRealm(config.getString("gui.realmgui.banned.name"),realm), (byte) config.getInt("gui.realmgui.banned.data"), Config.getListWithReplacementRealm(config.getStringList("gui.realmgui.banned.lore"),realm)).toItemStack(), e -> {
            e.setCancelled(true);
            e.getWhoClicked().closeInventory();
            new WholeGUI().openBanned(player, realm);
        });

        privacy = ClickableItem.of(new ItemsUtils(Config.getMaterial(config.getString("gui.realmgui.privacy.item")), Config.getStringWithReplacementRealm(config.getString("gui.realmgui.privacy.name"),realm), (byte) config.getInt("gui.realmgui.privacy.data") ,Config.getListWithReplacementRealm(config.getStringList("gui.realmgui.privacy.lore"),realm)).toItemStack(), e -> {
            e.setCancelled(true);
            if (RealmPlayer.getPlayer(player.getUniqueId().toString()).getRankByRealm(realm) == RealmRank.MANAGER || RealmPlayer.getPlayer(player.getUniqueId().toString()).getRankByRealm(realm) == RealmRank.OWNER) {
                if (realm.getPrivacy())
                    realm.setPrivacy(false);
                else
                realm.setPrivacy(true);
                player.sendMessage(Config.getStringWithReplacementRealm(config.getString("gui.realmgui.privacy.clickmessage"),realm));
                e.getWhoClicked().closeInventory();
            } else
                player.sendMessage("§cOnly the manager and the owner of the Realm can do this !");
        });

        members = ClickableItem.of(new ItemsUtils(Config.getMaterial(config.getString("gui.realmgui.members.item")), Config.getStringWithReplacementRealm(config.getString("gui.realmgui.members.name"),realm), (byte) config.getInt("gui.realmgui.members.data"),Config.getListWithReplacementRealm(config.getStringList("gui.realmgui.members.lore"),realm)).toItemStack(), e -> {
            e.setCancelled(true);
            e.getWhoClicked().closeInventory();
            new WholeGUI().openMembersGui(player, realm);
        });

        theme = ClickableItem.of(new ItemsUtils(Config.getMaterial(config.getString("gui.realmgui.theme.item")), Config.getStringWithReplacementRealm(config.getString("gui.realmgui.theme.name"),realm), (byte) config.getInt("gui.realmgui.theme.data"),Config.getListWithReplacementRealm(config.getStringList("gui.realmgui.theme.lore"),realm)).toItemStack(), e -> {
            e.setCancelled(true);
            e.getWhoClicked().closeInventory();
            new WholeGUI().openThemeGui(player, realm);
        });
        if (realm.getLevel().getNumber() >= 20) {
            upgrade = ClickableItem.of(new ItemsUtils(Config.getMaterial(config.getString("gui.realmgui.upgrade.item")), Config.getStringWithReplacementRealm(config.getString("gui.realmgui.upgrade.name"),realm), (byte) config.getInt("gui.realmgui.upgrade.data"), Config.getListWithReplacementRealm(config.getStringList("gui.realmgui.upgrade.maxlevellore"),realm)).toItemStack(), e -> {
                player.sendMessage("§cThis realm is already at max level.");
                e.setCancelled(true);
                player.closeInventory();
            });

    }
        else {
            RealmLevel nextlevel = RealmLevel.getLevel(realm.getLevel().getNumber() + 1);
            upgrade = ClickableItem.of(new ItemsUtils(Config.getMaterial(config.getString("gui.realmgui.upgrade.item")), Config.getStringWithReplacementRealm(config.getString("gui.realmgui.upgrade.name"),realm), (byte) config.getInt("gui.realmgui.upgrade.data"), Config.getListWithReplacementRealm(config.getStringList("gui.realmgui.upgrade.lore"),realm)).toItemStack(), e -> {
                e.setCancelled(true);
                e.getWhoClicked().closeInventory();
                    if (RealmPlayer.getPlayer(player.getUniqueId().toString()).getRankByRealm(realm) == RealmRank.MEMBER || RealmPlayer.getPlayer(player.getUniqueId().toString()).getRankByRealm(realm) == RealmRank.GUARD) {
                        player.sendMessage("§cOnly the manager and the owner of the Realm can do this !");
                        return;
                    }
                    if (realm.getMoney() < nextlevel.getPrice()) {
                        player.sendMessage("§cYou don't have enough money to do upgrade your Realm.");
                        player.sendMessage("§cYour realm have §6" + realm.getMoney() + " §cand it needs §6" + nextlevel.getPrice());
                        player.sendMessage("§cDo §e/realm addmoney <amount> §cto add some money to your realm");
                        return;
                    }

                    realm.addMoney(-nextlevel.getPrice());
                    realm.upgrade(realm.getLevel().getNumber() + 1);
                    for (RealmPlayer s : realm.getRealmMembers()) {
                        if (Bukkit.getPlayer(s.getName()) != null)
                            Bukkit.getPlayer(s.getName()).sendMessage("§e§l" + player.getName() + " §aupgraded §b§l" + realm.getOwner().getName() + "'s §aRealm to the level §e" + realm.getLevel().getNumber());
                    }
            });
            back = ClickableItem.of(new ItemsUtils(Config.getMaterial(config.getString("gui.back.item")), Config.getStringWithReplacementRealm(config.getString("gui.back.name"),realm), (byte) config.getInt("gui.back.data"), Config.getListWithReplacementRealm(config.getStringList("gui.back.lore"),realm)).toItemStack(), e -> {
                e.setCancelled(true);
                player.closeInventory();
                new WholeGUI().openAllRealmGUI(player);
            });
            basic = ClickableItem.of(new ItemsUtils(Config.getMaterial(config.getString("gui.default.item")),"", (byte) config.getInt("gui.default.data"), Collections.singletonList("")).toItemStack(), e -> e.setCancelled(true));
        }
    }

    @Override
    public void init(Player player, InventoryContents inventoryContents) {
        inventoryContents.fill(basic);
        inventoryContents.set(Config.getRowFromInt(config.getInt("gui.realmgui.home.slot")), Config.getCollumFromInt(config.getInt("gui.realmgui.home.slot")), teleport);
        inventoryContents.set(Config.getRowFromInt(config.getInt("gui.realmgui.privacy.slot")), Config.getCollumFromInt(config.getInt("gui.realmgui.privacy.slot")), privacy);
        inventoryContents.set(Config.getRowFromInt(config.getInt("gui.realmgui.upgrade.slot")), Config.getCollumFromInt(config.getInt("gui.realmgui.upgrade.slot")), upgrade);
        inventoryContents.set(Config.getRowFromInt(config.getInt("gui.realmgui.theme.slot")), Config.getCollumFromInt(config.getInt("gui.realmgui.theme.slot")), theme);
        inventoryContents.set(Config.getRowFromInt(config.getInt("gui.realmgui.members.slot")), Config.getCollumFromInt(config.getInt("gui.realmgui.members.slot")), members);
        inventoryContents.set(Config.getRowFromInt(config.getInt("gui.realmgui.banned.slot")), Config.getCollumFromInt(config.getInt("gui.realmgui.banned.slot")), banned);
        if (from) {
            inventoryContents.set(Config.getRowFromInt(config.getInt("gui.back.slot")), Config.getCollumFromInt(config.getInt("gui.back.slot")), back);
        }
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }

}
