package com.chillguy.simplerealm.gui;

import com.chillguy.simplerealm.realm.RealmPlayer;
import com.chillguy.simplerealm.realm.Realm;
import com.chillguy.simplerealm.utils.Config;
import com.chillguy.simplerealm.utils.ItemsUtils;
import com.venned.simpleinventory.ClickableItem;
import com.venned.simpleinventory.content.InventoryContents;
import com.venned.simpleinventory.content.InventoryProvider;
import com.venned.simpleinventory.content.SlotIterator;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class MembersProvider implements InventoryProvider {
    private Player player;
    private Realm realm;
    private YamlConfiguration config;

    public MembersProvider(Player player, Realm realms) {
        this.player = player;
        this.realm = realms;
        this.config = Config.ASPECT.getConfig();
    }


    @Override
    public void init(Player player, InventoryContents inventoryContents) {
        ClickableItem basic = ClickableItem.of(new ItemStack(Material.BLACK_STAINED_GLASS_PANE,1), e -> e.setCancelled(true));
        inventoryContents.fill(basic);
        for (RealmPlayer rp : realm.getRealmMembers()) {
            setItem(inventoryContents,ClickableItem.of(ItemsUtils.getHead(rp.getName(), Config.getStringWithReplacementPlayer(config.getString("gui.membersgui.player.name"),realm,rp), Config.getListWithReplacementPlayer(config.getStringList("gui.membersgui.player.lore"),realm,rp)),e ->{
                e.setCancelled(true);
                new WholeGUI().openRankGui(rp,player,realm);
            }));
        }
        inventoryContents.set(4,0,ClickableItem.of(new ItemsUtils(Config.getMaterial(config.getString("gui.back.item")), Config.getStringWithReplacementRealm(config.getString("gui.back.name"),realm), (byte) config.getInt("gui.back.data"), Config.getListWithReplacementRealm(config.getStringList("gui.back.lore"),realm)).toItemStack(), e ->{
            e.setCancelled(true);
            player.closeInventory();
            new WholeGUI().openRealmGui(player,realm,false);
        }));
    }

    private void setItem(InventoryContents inventoryContents,ClickableItem clickableItem) {
        SlotIterator iterator = inventoryContents.newIterator(SlotIterator.Type.HORIZONTAL,0,0);
        while(!iterator.ended()){
            if(iterator.get().isPresent()){
                if(iterator.get().get().getItem().getType() == Material.BLACK_STAINED_GLASS_PANE){
                    inventoryContents.set(iterator.row(),iterator.column(),clickableItem);
                    return;
                }
            }
            iterator.next();
        }

    }


    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }

}
