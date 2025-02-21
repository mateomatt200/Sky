package com.chillguy.simplerealm.gui;

import com.chillguy.simplerealm.realm.Realm;
import com.chillguy.simplerealm.realm.RealmPlayer;
import com.chillguy.simplerealm.utils.ItemsUtils;
import com.venned.simpleinventory.ClickableItem;
import com.venned.simpleinventory.content.InventoryContents;
import com.venned.simpleinventory.content.InventoryProvider;
import com.venned.simpleinventory.content.SlotIterator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class BannedProvider implements InventoryProvider {

    private RealmPlayer realmPlayer;
    private ArrayList<ClickableItem> items = new ArrayList<>();
    private Realm realm;
    private ClickableItem up, down;

    public BannedProvider(Player player, Realm realm) {
        this.realmPlayer = RealmPlayer.getPlayer(player.getUniqueId().toString());
        this.realm = realm;
    }


    @Override
    public void init(Player player, InventoryContents inventoryContents) {
        ClickableItem basic = ClickableItem.of(new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1), e -> e.setCancelled(true));
        inventoryContents.fill(basic);
        for (RealmPlayer rp : realm.getBanned()) {
            setItem(inventoryContents, ClickableItem.of(ItemsUtils.getHead(rp.getName(), "§b" + rp.getName(), Arrays.asList("", "§eClick to unban " + rp.getName())), e -> {
                e.setCancelled(true);
                realm.unbanPlayer(rp);
                player.sendMessage("§aSuccessfully unbanned the player from the Realm.");
                player.closeInventory();
                for(RealmPlayer s : realm.getRealmMembers()){
                    if(Bukkit.getPlayer(s.getName()) != null)
                        player.sendMessage("§l§e"+rp.getName()+" §a has been unbanned from §l§b"+realm.getOwner().getName()+"'s §aRealm.");
                }
            }));
        }
        inventoryContents.set(4, 0, ClickableItem.of(new ItemsUtils(Material.BLACK_BED, "⬅ §bGo back", Arrays.asList("", "§7Click to go back to the", "§7Realm options.")).toItemStack(), e -> {
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


