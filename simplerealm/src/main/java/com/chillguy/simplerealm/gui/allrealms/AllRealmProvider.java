package com.chillguy.simplerealm.gui.allrealms;

import com.chillguy.simplerealm.realm.RealmPlayer;
import com.chillguy.simplerealm.gui.WholeGUI;
import com.chillguy.simplerealm.realm.Realm;
import com.chillguy.simplerealm.utils.ItemsUtils;
import com.venned.simpleinventory.ClickableItem;
import com.venned.simpleinventory.content.InventoryContents;
import com.venned.simpleinventory.content.InventoryProvider;
import com.venned.simpleinventory.content.Pagination;
import com.venned.simpleinventory.content.SlotIterator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AllRealmProvider implements InventoryProvider {

    private RealmPlayer realmPlayer;
    private ArrayList<ClickableItem> items = new ArrayList<>();
    private ClickableItem up, down;

    public AllRealmProvider(Player player) {
        this.realmPlayer = RealmPlayer.getPlayer(player.getUniqueId().toString());
    }


    @Override
    public void init(Player player, InventoryContents inventoryContents) {
        Pagination pagination = inventoryContents.pagination();
        int i = 0;
        ClickableItem basic = ClickableItem.of(new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1), e -> e.setCancelled(true));
        if (realmPlayer.getAllRealm().size() <= 27)
            inventoryContents.fill(basic);
        List<Realm> realms = realmPlayer.getAllRealm();
        if(realmPlayer.getOwned() != null){
            realms.removeIf(realm -> realmPlayer.getOwned() == realm);
            realms.add(0,realmPlayer.getOwned());
        }
        for (Realm r : realms) {
            ClickableItem cl = ClickableItem.of(new ItemsUtils(Material.DARK_OAK_DOOR
                    , "§b" + r.getOwner().getName() + "'s Realm."
                    , Arrays.asList("§7Rank: " + realmPlayer.getRankByRealm(r).getColor() + realmPlayer.getRankByRealm(r).toString(), "§7Privacy: §6" + r.getPrivacyString(), "§7# of members §6" + r.getRealmMembers().size() + "§7/§6" + r.getLevel().getMaxplayer())).toItemStack(), e -> {
                e.setCancelled(true);
                player.closeInventory();
                new WholeGUI().openRealmGui(player, r,true);
            });
            if (realmPlayer.getOwned() == r) {
                cl.getItem().setType(Material.BLACK_BED);
            }
            if (realmPlayer.getAllRealm().size() <= 27)
                placeIfVoid(cl, inventoryContents);
            else {
                up = ClickableItem.of(new ItemsUtils(Material.ARROW, "§bNext page", Arrays.asList("§7Click to go to ", "§7the page " + getNextPage(pagination))).toItemStack(), e -> {
                    e.setCancelled(true);
                    new WholeGUI().getAllRealmGUI(player).open(player, pagination.next().getPage());
                });
                down = ClickableItem.of(new ItemsUtils(Material.ARROW, "§bPrevious page", Arrays.asList("§7Click to go to ", "§7the page " + getPreviousPage(pagination))).toItemStack(), e -> {
                    e.setCancelled(true);
                    new WholeGUI().getAllRealmGUI(player).open(player, pagination.previous().getPage());
                });
                i++;
                int j = i;
                if (j > 27 && (j - 1) % 27 == 0) {
                    items.add(down);
                } else if (j % 27 == 0) {
                    items.add(up);
                }
                //items.add(cl);
                else{
                    items.add(cl);
                }

            }

        }
        if (realmPlayer.getAllRealm().size() > 27) {
            while (items.size() % 27 != 0) {
                items.add(basic);
            }
            pagination.setItems(items.toArray(new ClickableItem[items.size()]));
            pagination.setItemsPerPage(27);
            pagination.addToIterator(inventoryContents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0));
        }

    }

    public void placeIfVoid(ClickableItem clickableItem, InventoryContents inventoryContents) {
        SlotIterator iterator = inventoryContents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0);
        while (!iterator.ended()) {
            if (iterator.get().isPresent() && iterator.get().get().getItem().getType() == Material.BLACK_STAINED_GLASS_PANE) {
                inventoryContents.set(iterator.row(), iterator.column(), clickableItem);
                return;
            }
            iterator.next();
        }
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }

    private int getPreviousPage(Pagination pagination) {
        int i = pagination.getPage();
        return i;
    }

    private int getNextPage(Pagination pagination) {
        int i = pagination.getPage();
        i++;
        return i;
    }
}

