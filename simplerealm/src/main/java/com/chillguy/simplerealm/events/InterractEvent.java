package com.chillguy.simplerealm.events;

import com.chillguy.simplerealm.realm.Realm;
import com.chillguy.simplerealm.realm.RealmLevel;
import com.chillguy.simplerealm.realm.RealmPlayer;
import com.chillguy.simplerealm.realm.RealmRank;
import com.chillguy.simplerealm.utils.Config;
import com.chillguy.simplerealm.utils.ConfigFiles;
import com.chillguy.simplerealm.utils.blocks.BlockLimitation;
import com.chillguy.simplerealm.utils.blocks.BlockSave;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InterractEvent implements Listener {
    YamlConfiguration config = Config.ASPECT.getConfig();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        RealmPlayer realmPlayer = RealmPlayer.getPlayer(player.getUniqueId().toString());
        if (Realm.getRealmFromLocation(event.getBlock().getLocation()) != null) {
            Realm realm = Realm.getRealmFromLocation(event.getBlock().getLocation());


            //new RealmBreakEvent(realm, realmPlayer, player, event.getBlock());
            if (player.hasPermission("realm.bypass")) {
                BlockSave blockSave = realm.getBlockSaves().stream().filter(c->c.getMaterial() == event.getBlock().getType()).findFirst().orElse(null);
                if(blockSave != null) {
                    blockSave.decrementQuantity();
                }
                return;
            }

            if (!realm.getRealmMembers().contains(realmPlayer)) {
                event.setCancelled(true);
            }

            RealmRank rank = realmPlayer.getRankByRealm(realm);
            if(rank == RealmRank.MEMBER){
                event.setCancelled(true);
                return;
            }


            BlockSave blockSave = realm.getBlockSaves().stream().filter(c->c.getMaterial() == event.getBlock().getType()).findFirst().orElse(null);
            if(blockSave != null) {
                blockSave.decrementQuantity();
            }

            if (realm.getTheme().getCuboid().containsLocation(event.getBlock().getLocation())) {
                if (!config.getString("messages.interact.nobreak").isEmpty() && config.getString("messages.interact.nobreak") != null)
                    player.sendMessage(config.getString("messages.interact.nobreak").replace("&","§"));
                event.setCancelled(true);
            }
        }


    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBuild(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        RealmPlayer realmPlayer = RealmPlayer.getPlayer(player.getUniqueId().toString());
        if (player.hasPermission("realm.bypass")) {
            if (Realm.getRealmFromLocation(event.getBlock().getLocation()) != null) {
                Realm realm = Realm.getRealmFromLocation(event.getBlock().getLocation());
                boolean isContain = realm.getLevel().getLimitations().stream().anyMatch(c -> c.getBlock() == event.getBlock().getType());

                if (isContain) {
                    BlockSave blockSave = realm.getBlockSaves().stream().filter(c -> c.getMaterial() == event.getBlock().getType()).findFirst().orElse(null);
                    if (blockSave != null) {
                        int current = blockSave.getQuantity();
                        int max = countBlockTypeLimitations(realm.getLevel(), event.getBlock().getType());
                        if (max != 0) {
                            if (current >= max) { // Si excede el límite
                                event.setCancelled(true); // Cancela el evento
                                player.sendMessage("§c§l(!) §cYou have reached the limit of " + max + " blocks of " + event.getBlock().getType().name() + " in your Realm.");
                                return;
                            }
                        }
                        blockSave.increaseQuantity();

                    } else {
                        BlockSave newBlockSave = new BlockSave(event.getBlock().getType(), 1);
                        realm.getBlockSaves().add(newBlockSave);
                    }
                }
            }

            return;
        }
        if (Realm.getRealmFromLocation(event.getBlock().getLocation()) != null) {
            Realm realm = Realm.getRealmFromLocation(event.getBlock().getLocation());

            if (!realm.getRealmMembers().contains(realmPlayer)) {
                if (!config.getString("messages.interact.nobuild").isEmpty() && config.getString("messages.interact.nobuild") != null)
                    player.sendMessage(config.getString("messages.interact.nobuild").replace("&","§"));
                event.setCancelled(true);
                return;
            }

            RealmRank rank = realmPlayer.getRankByRealm(realm);
            if(rank == RealmRank.MEMBER){
                event.setCancelled(true);
                return;
            }

            boolean isContain = realm.getLevel().getLimitations().stream().anyMatch(c->c.getBlock() == event.getBlock().getType());

            if(isContain) {
                BlockSave blockSave = realm.getBlockSaves().stream().filter(c -> c.getMaterial() == event.getBlock().getType()).findFirst().orElse(null);
                if (blockSave != null) {
                    int current = blockSave.getQuantity();
                    int max = countBlockTypeLimitations(realm.getLevel(), event.getBlock().getType());
                    if (max != 0) {
                        if (current >= max) { // Si excede el límite
                            event.setCancelled(true); // Cancela el evento
                            player.sendMessage("§c§l(!) §cYou have reached the limit of " + max + " blocks of " + event.getBlock().getType().name() + " in your Realm.");
                            return;
                        }
                    }
                    blockSave.increaseQuantity();

                } else {
                    BlockSave newBlockSave = new BlockSave(event.getBlock().getType(), 1);
                    realm.getBlockSaves().add(newBlockSave);
                }
            }

//            new RealmBuildEvent(realm, realmPlayer, player, event.getBlock());
            if (checkBlockInRealm(event.getBlockPlaced().getLocation(), realm)) {
                if (!config.getString("messages.interact.nobuild").isEmpty() && config.getString("messages.interact.nobuild") != null)
                    player.sendMessage(config.getString("messages.interact.nobuild").replace("&","§"));
                event.setCancelled(true);
            }
            else if (!realm.isInRealm(event.getBlockPlaced().getLocation())) {
                if (!config.getString("messages.interact.nobuild").isEmpty() && config.getString("messages.interact.nobuild") != null)
                    player.sendMessage(config.getString("messages.interact.nobuild").replace("&","§"));
                event.setCancelled(true);
            }


        }
        else{
            if(player.getLocation().getWorld() == ConfigFiles.getWorld()){
                if (!config.getString("messages.interact.nobreak").isEmpty() && config.getString("messages.interact.nobreak") != null)
                    player.sendMessage(config.getString("messages.interact.nobreak").replace("&","§"));
                event.setCancelled(true);
            }
        }
    }

    public int countBlockTypeLimitations(RealmLevel realmLevel, Material material){
        BlockLimitation limitation = realmLevel.getLimitations().stream().filter(c->c.getBlock() == material).findFirst().orElse(null);
        if(limitation != null){
            return limitation.getQuantity();
        } else {
            return 0;
        }
    }

    public int countBlockTypeInCuboid(List<Block> blocks, Material type) {
        int count = 0;
        for (Block block : blocks) {
            if (block.getType() == type) {
                count++;
            }
        }
        return count;
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        if (Realm.getRealmFromLocation(event.getFrom()) != null) {
            Realm realm = Realm.getRealmFromLocation(event.getFrom());
            if (realm.getTheme().getCuboid().containsLocation(event.getFrom())) {
                event.setCancelled(true);
                event.setTo(ConfigFiles.getSpawn());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChest(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        RealmPlayer realmPlayer = RealmPlayer.getPlayer(player.getUniqueId().toString());
        if (player.hasPermission("realm.bypass")) {
            return;
        }
        if (event.getAction() == Action.PHYSICAL) {
            if (Realm.getRealmFromLocation(event.getClickedBlock().getLocation()) != null) {
                Realm realm = Realm.getRealmFromLocation(event.getClickedBlock().getLocation());
                if (!realm.getRealmMembers().contains(realmPlayer)) {
                    if (!config.getString("messages.interact.nointeract").isEmpty() && config.getString("messages.interact.nointeract") != null)
                        player.sendMessage(config.getString("messages.interact.nointeract").replace("&","§"));
                    event.setCancelled(true);
                }
            }
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock() != null && Realm.getRealmFromLocation(event.getClickedBlock().getLocation()) != null) {
                Realm realm = Realm.getRealmFromLocation(event.getClickedBlock().getLocation());
                if (!realm.getRealmMembers().contains(realmPlayer) && event.getClickedBlock().getType() != Material.ACACIA_SIGN && event.getClickedBlock().getType() != Material.ACACIA_WALL_SIGN) {
                    event.setCancelled(true);
                }
                if (realm.getRealmMembers().contains(realmPlayer) && event.getClickedBlock().getType() == Material.CHEST && realmPlayer.getRankByRealm(realm) == RealmRank.MEMBER) {
                    event.setCancelled(true);
                    if (!config.getString("messages.interact.nochest").isEmpty()&& config.getString("messages.interact.nochest") != null)
                        player.sendMessage(config.getString("messages.interact.nochest").replace("&","§"));
                }
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Iterator<Block> iter = event.blockList().iterator();
        while (iter.hasNext()) {
            Block b = iter.next();
            if (Realm.getRealmFromLocation(b.getLocation()) != null) {
                Realm realm = Realm.getRealmFromLocation(b.getLocation());
                if (realm.getTheme().getCuboid().containsLocation(b.getLocation())) {
                    iter.remove();
                }
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (Realm.getRealmFromLocation(player.getLocation()) != null) {
            Realm realm = Realm.getRealmFromLocation(player.getLocation());
            //new PlayerMoveInRealmEvent(realm,RealmPlayer.getPlayer(player.getUniqueId().toString()),player);
            if (player.getLocation().getBlockY() <= -1) {
                realm.teleportToSpawn(player);
            }
            if (player.getLocation().getBlock().getType() == Material.END_PORTAL) {
                new ConfigFiles().sendToSpawn(player);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Arrow && (((Arrow) event.getDamager()).getShooter() instanceof Player)) {
            Player shooter = (Player) (((Arrow) event.getDamager()).getShooter());
            if (shooter.hasPermission("realm.bypass")) {
                return;
            }
            RealmPlayer realmPlayer = RealmPlayer.getPlayer(shooter.getUniqueId().toString());
            if (Realm.getRealmFromLocation(event.getDamager().getLocation()) != null) {
                Realm realm = Realm.getRealmFromLocation(event.getDamager().getLocation());
                if (!realm.getRealmMembers().contains(realmPlayer)) {
                    shooter.sendMessage("§cYou are not in this realm");
                    event.setCancelled(true);
                }

            }
        }
        if (event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            if (damager.hasPermission("realm.bypass")) {
                return;
            }
            RealmPlayer realmPlayer = RealmPlayer.getPlayer(damager.getUniqueId().toString());
            if (Realm.getRealmFromLocation(event.getDamager().getLocation()) != null) {
                Realm realm = Realm.getRealmFromLocation(event.getDamager().getLocation());
                if (!realm.getRealmMembers().contains(realmPlayer)) {
                    if (!config.getString("messages.interact.notinrealm").isEmpty()&& config.getString("messages.interact.notinrealm") != null)
                        damager.sendMessage(config.getString("messages.interact.notinrealm").replace("&","§"));
                    event.setCancelled(true);
                }

            }
        } else {
            if (Realm.getRealmFromLocation(event.getEntity().getLocation()) != null && event.getEntity() instanceof Player) {
                Realm realm = Realm.getRealmFromLocation(event.getEntity().getLocation());
                if (realm.getTheme().getCuboid().containsLocation(event.getEntity().getLocation())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractAtEntityEvent event) {
        RealmPlayer realmPlayer = RealmPlayer.getPlayer(event.getPlayer().getUniqueId().toString());
        if (event.getPlayer().hasPermission("realm.bypass")) {
            return;
        }
        if (Realm.getRealmFromLocation(event.getRightClicked().getLocation()) != null) {
            Realm realm = Realm.getRealmFromLocation(event.getRightClicked().getLocation());
            if (!realm.getRealmMembers().contains(realmPlayer)) {
                if (!config.getString("messages.inteact.nointeract").isEmpty()&& config.getString("messages.interact.nointeract") != null)
                    event.getPlayer().sendMessage(config.getString("messages.inteact.nointeract").replace("&","§"));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (player.hasPermission("realm.bypass")) {
                return;
            }
            if (Realm.getRealmFromLocation(player.getLocation()) != null) {
                Realm realm = Realm.getRealmFromLocation(player.getLocation());
                if (!realm.getRealmMembers().contains(RealmPlayer.getPlayer(player.getUniqueId().toString()))) {
                    event.setCancelled(true);
                }
                if (realm.getTheme().getCuboid().containsLocation(player.getLocation())) {
                    event.setCancelled(true);
                }
            }
        }

    }

    @EventHandler
    public void mobSpawn(CreatureSpawnEvent event) {
        if (Realm.getRealmFromLocation(event.getEntity().getLocation()) != null) {
            Realm realm = Realm.getRealmFromLocation(event.getEntity().getLocation());
            if (realm.getTheme().getCuboid().containsLocation(event.getEntity().getLocation())) {
                event.setCancelled(true);
            }

        }
    }

    private boolean checkBlockInRealm(Location l, Realm r) {
        if (r.getTheme().getCuboid().containsLocation(l)) {
            return true;
        }
        return false;
    }

    private void useless() {
        ArrayList<String> strs = new ArrayList<>();
        strs.forEach(System.out::println);
    }
}
