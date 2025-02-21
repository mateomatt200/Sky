package com.chillguy.simplerealm.gui.allrealms;


import com.chillguy.simplerealm.Main;
import com.chillguy.simplerealm.realm.RealmPlayer;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import com.chillguy.simplerealm.gui.WholeGUI;
import com.chillguy.simplerealm.realm.Realm;
import com.chillguy.simplerealm.utils.ItemsUtils;
import com.venned.simpleinventory.ClickableItem;
import com.venned.simpleinventory.content.InventoryContents;
import com.venned.simpleinventory.content.InventoryProvider;
import com.venned.simpleinventory.content.Pagination;
import com.venned.simpleinventory.content.SlotIterator;
import net.ess3.api.MaxMoneyException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class AllMoneyProvider implements InventoryProvider {
    private final RealmPlayer realmPlayer;
    private final ArrayList<ClickableItem> items = new ArrayList<>();
    private ClickableItem up, down;
    private int money;

    public AllMoneyProvider(Player player,int money) {
        this.realmPlayer = RealmPlayer.getPlayer(player.getUniqueId().toString());
        this.money = money;

    }

    @Override
    public void init(Player player, InventoryContents inventoryContents) {
        Pagination pagination = inventoryContents.pagination();
        int i = 1;
        ClickableItem basic = ClickableItem.of(new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1), e -> e.setCancelled(true));
        if (realmPlayer.getAllRealm().size() <= 9)
            inventoryContents.fill(basic);
        for (Realm r : realmPlayer.getAllRealm()) {


            ClickableItem kick = ClickableItem.of(ItemsUtils.getHead(r.getOwner().getName(),"§bSend money to "+r.getOwner().getName()+"'s Realm.",Arrays.asList("§7# of members §6" + r.getRealmMembers().size() + "§7/§6" + r.getLevel().getMaxplayer(), "§7Privacy: §6" + r.getPrivacyString(),"§7Money: §e"+r.getMoney())), e -> {
                e.setCancelled(true);
                player.closeInventory();
                try {
                    //Main.economy.hasAccount(player) &&
                    if  (Main.getInstance().getBalance(player) < money) {
                        player.sendMessage("§cYou don't have enough money to do this");
                        player.sendMessage("§cYou have §6" + Main.getInstance().getBalance(player) + "$ §cand you need §6" + money);
                        return;
                    }
                } catch (UserDoesNotExistException ex) {
                    throw new RuntimeException(ex);
                }
                r.addMoney(money);
                try {
                    Main.getInstance().withDraw(player,money);
                } catch (UserDoesNotExistException ex) {
                    throw new RuntimeException(ex);
                } catch (MaxMoneyException ex) {
                    throw new RuntimeException(ex);
                } catch (NoLoanPermittedException ex) {
                    throw new RuntimeException(ex);
                }
                player.sendMessage("§aYou successfully sent §e"+money+"$ to §b"+r.getOwner().getName()+"'s §arealm");
                for(RealmPlayer s : r.getRealmMembers()){
                    if(Bukkit.getPlayer(s.getName()) != null)
                        Bukkit.getPlayer(s.getName()).sendMessage("§e§l"+player.getName()+"§7 just sent §e"+money+"$§7 to §b"+r.getOwner().getName()+"'s §7realm");
                }
            });


            if (realmPlayer.getOwned() == r) {
                System.out.println("[DEBUG] Realm Player Owner ");
                kick.getItem().setType(Material.BLACK_BED);
            }
            if (realmPlayer.getAllRealm().size() <= 9) {
                System.out.println("[DEBUG] Realm Player PlaceIfVoid ");
                placeIfVoid(kick, inventoryContents);
            } else {
                System.out.println("[DEBUG] Agregando botones de navegación en el inventario."); // Debug

                up = ClickableItem.of(new ItemsUtils(Material.ARROW, "§bNext page", Arrays.asList("§7Click to go to ", "§7the page " + getNextPage(pagination))).toItemStack(), e -> {

                    e.setCancelled(true);
                    new WholeGUI().getAllMoneyGUI(player,money).open(player, pagination.next().getPage());
                });
                down = ClickableItem.of(new ItemsUtils(Material.ARROW, "§bPrevious page", Arrays.asList("§7Click to go to ", "§7the page " + getPreviousPage(pagination))).toItemStack(), e -> {
                    e.setCancelled(true);
                    new WholeGUI().getAllMoneyGUI(player,money).open(player, pagination.previous().getPage());
                });
                i++;
                int j = i;

                System.out.println("[DEBUG] Procesando elemento con índice: " + j); // Debug


                if (j > 9 && (j - 1) % 9 == 0) {
                    System.out.println("[DEBUG] Agregando botón 'Página anterior' al inventario."); // Debug

                    items.add(down);
                } else if (j % 9 == 0) {
                    System.out.println("[DEBUG] Agregando botón 'Siguiente página' al inventario."); // Debug


                    items.add(up);
                } else {
                    System.out.println("[DEBUG] Agregando un elemento normal al inventario. " + items.toString()); // Debug
                    items.add(kick);
                }
            }
        }
        if (realmPlayer.getAllRealm().size() > 9) {
            System.out.println("[DEBUG] Más de 9 reinos detectados. Configurando paginación."); // Debug

            items.add(0, down);
            items.add(items.size(), up);

            pagination.setItems(items.toArray(new ClickableItem[0]));
            pagination.setItemsPerPage(9);
            pagination.addToIterator(inventoryContents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0));
        }

    }

    public void placeIfVoid(ClickableItem clickableItem, InventoryContents inventoryContents) {
        SlotIterator iterator = inventoryContents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0);
        while (!iterator.ended()) {
            // Log de posición actual en el iterador
            System.out.println("[DEBUG] Iterador en fila: " + iterator.row() + ", columna: " + iterator.column());

            if (iterator.get().isPresent()) {
                System.out.println("[DEBUG] Present");
                ItemStack item = iterator.get().get().getItem();
                if (item != null && item.getType() == Material.GREEN_STAINED_GLASS_PANE) {
                    inventoryContents.set(iterator.row(), iterator.column(), clickableItem);
                    Bukkit.getLogger().info("[DEBUG] Item colocado en fila: " + iterator.row() + ", columna: " + iterator.column());
                    return;
                }
            }

            if (!iterator.get().isPresent()) { // Verifica si el slot está vacío
                System.out.println("[DEBUG] Slot vacío encontrado en fila: " + iterator.row() + ", columna: " + iterator.column());
                inventoryContents.set(iterator.row(), iterator.column(), clickableItem);
                System.out.println("[DEBUG] Ítem colocado en fila: " + iterator.row() + ", columna: " + iterator.column());
                return; // Salir del bucle después de colocar el ítem
            }
            iterator.next(); // Avanza al siguiente slot
        }
        System.out.println("[DEBUG] No se encontró ningún slot vacío para colocar el ítem.");
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
    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
