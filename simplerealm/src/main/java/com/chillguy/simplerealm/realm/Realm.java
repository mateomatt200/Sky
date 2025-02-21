package com.chillguy.simplerealm.realm;

import com.chillguy.simplerealm.Main;
import com.chillguy.simplerealm.realm.gen.GenLevel;
import com.chillguy.simplerealm.realm.themes.Theme;
import com.chillguy.simplerealm.realm.themes.ThemeType;
import com.chillguy.simplerealm.utils.CuboidUtils;
import com.chillguy.simplerealm.utils.SchematicUtils;
import com.chillguy.simplerealm.utils.WorldBorder;
import com.chillguy.simplerealm.utils.ConfigFiles;
import com.chillguy.simplerealm.utils.blocks.BlockLimitation;
import com.chillguy.simplerealm.utils.blocks.BlockSave;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.io.File;
import java.util.*;

public class Realm {
    public static Set<Realm> allrealm = new HashSet<>();
    private RealmLevel level;
    private boolean privacy;
    private CuboidUtils cuboid;
    private String perk;
    private Theme theme;
    private int vote;
    private int money;
    private ArrayList<RealmPlayer> realmmembers = new ArrayList<>();
    private ArrayList<RealmPlayer> banned = new ArrayList<>();

    private Set<BlockSave> blockSaves = new HashSet<>();

    private RealmPlayer owner;
    private String rank;
    private GenLevel genLevel;

    public Realm(RealmPlayer rp, ThemeType theme, Location location, int level, int vote, String rank, GenLevel genLevel) {
        owner = rp;
        this.rank = rank;
        this.genLevel = genLevel;
        setLevel(level);

        privacy = false;
        this.theme = new Theme(theme, location);
        this.vote = vote;
        setCuboid();

        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
            List<Block> blocks = getCuboid().getBlocks();
            List<BlockLimitation> materials = getLevel().limitations;
            for(Block block : blocks) {
                if(materials.stream().anyMatch(c->c.getBlock() == block.getType())){
                    if(blockSaves.stream().anyMatch(c->c.getMaterial() == block.getType())){
                        BlockSave blockSave = blockSaves.stream().filter(c->c.getMaterial() == block.getType()).findFirst().get();
                        blockSave.increaseQuantity();
                        continue;
                    }
                    BlockSave blockSave = new BlockSave(block.getType(), 1);
                    blockSaves.add(blockSave);
                }
            }

        }, 120L);



        allrealm.add(this);
        addPlayer(rp);
        promote(rp, RealmRank.OWNER);
        rp.setOwned(this);

    }

    public static void deleteAll(){
        allrealm.clear();
    }



    public void setGenLevel(GenLevel genLevel) {
        this.genLevel = genLevel;
    }

    public GenLevel getGenLevel() {
        return genLevel;
    }

    public String getRank() {
        return rank;
    }

    public Set<BlockSave> getBlockSaves() {
        return blockSaves;
    }

    public void addPlayer(RealmPlayer p) {
        if (!(realmmembers.size() >= level.getMaxplayer())) {
            realmmembers.add(p);
            p.addRealm(this);
            new RealmConfig().addPlayer(p, this);
        }
    }

    public void upgrade(int i) {
        setLevel(i);
        setCuboid();
        new RealmConfig().setLevel(this);
        sendBorderToAll();
    }

    public void spawnTheme() {
        theme.spawnTheme();
    }

    public void teleportToSpawn(Player player) {
        player.setFallDistance(0);
        player.teleport(theme.getSpawn().clone().add(0.5, 1, 0.5));
        sendWorldBorderPacket(player);
    }

    public void sendBorderToAll() {

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (cuboid.containsLocation(player.getLocation())) {
                sendWorldBorderPacket(player);
            }
        }
    }


    public Location getCenter() {

        if(level.getBordersize() != Main.getInstance().getRealmLevel().getLevelByRank(getLevel().getNumber(), getRank()).getBordersize()) {
            level = Main.getInstance().getRealmLevel().getLevelByRank(getLevel().getNumber(), getRank());
            int i = level.getBordersize() - Main.getInstance().getRealmLevel().getLevelByRank(getLevel().getNumber(), getRank()).getWidth();
            Location center = theme.getSpawn().clone().add(0, 0, i + theme.getThemeType().getNblock() - 4.5 * level.getNumber());
            return center.clone();
        }
        int i = level.getBordersize() - Main.getInstance().getRealmLevel().getLevelByRank(getLevel().getNumber(), getRank()).getWidth();
        Location center = theme.getSpawn().clone().add(0, 0, i + theme.getThemeType().getNblock() - 4.5 * level.getNumber());
        return center;
    }

    public void sendWorldBorderPacket(Player player) {
        WorldBorder.sendBorder(getCenter(), level.getBordersize(), player);
    }

    public Theme getTheme() {
        return theme;
    }

    public void kickPlayer(RealmPlayer player) {
        if (realmmembers.contains(player)) {
            realmmembers.remove(player);
            if (Bukkit.getPlayer(player.getUniqueId()) != null) {
                Player kicked = Bukkit.getPlayer(player.getUniqueId());
                if (cuboid.containsLocation(kicked.getLocation())) {
                    new ConfigFiles().sendToSpawn(kicked);
                }
            }

            player.remove(this);
            demote(player);
            new RealmConfig().removePlayer(player, this);
        }
    }

    public void setCuboid() {
        Location center = getCenter();


        Location loc1 = center.clone().add(-(level.getBordersize() / 2), 300, -((level.getWidth() / 2)));
        Location loc2 = center.clone().add(level.getBordersize() / 2, -300, (level.getWidth() / 2));




        int i = level.getBordersize() - Main.getInstance().getRealmLevel().getLevelByRank(getLevel().getNumber(), getRank()).getWidth();
        Location themeE = theme.getSpawn().clone().add(0, 0, i + theme.getThemeType().getNblock() - 4.5 * level.getNumber());
        Location newLoc1 = loc1.clone();
        newLoc1.setZ(themeE.getZ() + level.getMove_border());

        //Location newLoc1 = loc1.clone().add(0, 0, -20);


        int math = Math.round((float) level.getWidth() / 2);
        Location newLoc2 = loc2.clone().add(1, 0, math + level.getAdjustment());

        cuboid = new CuboidUtils(newLoc1, newLoc2);
    }

    public void banPlayer(RealmPlayer player) {
        if (realmmembers.contains(player)) {
            kickPlayer(player);
        }
        if (!banned.contains(player)) {
            banned.add(player);
            new RealmConfig().banPlayer(player, this);
        }
    }

    public void unbanPlayer(RealmPlayer player) {
        if (banned.contains(player)) {
            banned.remove(player);
            new RealmConfig().unbanPlayer(player, this);
        }

    }

    public void fillChest() {
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), ()-> {
            int i = 0;
            for (Block block : cuboid.getBlocks()) {
                i++;
                if (block.getLocation().getChunk().isLoaded()) {
                    block.getLocation().getChunk().load();
                }
                Block fromloc = block.getLocation().getBlock();
                if (fromloc.getType() == Material.CHEST) {
                    System.out.println("Found one chest");
                    Chest chest = (Chest) fromloc.getState();
                    if (ConfigFiles.getRealmchest() == null) {
                        System.out.println("Filling it vanilla");
                        chest.getBlockInventory().setItem(2, new ItemStack(Material.CARROT));
                        chest.getBlockInventory().setItem(3, new ItemStack(Material.SUGAR_CANE));
                        chest.getBlockInventory().setItem(5, new ItemStack(Material.MELON_STEM));
                        chest.getBlockInventory().setItem(6, new ItemStack(Material.ICE));
                        chest.getBlockInventory().setItem(13, new ItemStack(Material.TORCH, 8));
                        chest.getBlockInventory().setItem(20, new ItemStack(Material.POTATOES));
                        chest.getBlockInventory().setItem(21, new ItemStack(Material.CACTUS));
                        chest.getBlockInventory().setItem(23, new ItemStack(Material.PUMPKIN));
                        chest.getBlockInventory().setItem(24, new ItemStack(Material.LAVA_BUCKET));
                    } else {
                        System.out.println("Filling it custom");
                        chest.getBlockInventory().setContents(ConfigFiles.getRealmchest().getContents());
                    }

                }
            }
            System.out.println("Loooped though "+i+" blocks");

        }, 90L);
    }

    public void promote(RealmPlayer player, RealmRank rank) {
        if (realmmembers.contains(player)) {
            demote(player);
        }
        player.rankbyrealm.put(this, rank);
        new RealmConfig().promotePlayer(player, this);

    }

    public void pasteIsland() {
        try {
            File file = new File(Main.getInstance().getDataFolder(), "island.schematic");
            new SchematicUtils(theme.getSpawn(),file).paste();
            } catch (Exception e) {
            System.out.println("§c[AdvancedRealm] failed to load schematic, an error occured , please try to reinstall the plugin or call @iPazu#3982 on discord \n cause: " + e.getCause() + "\n trace:");
            e.printStackTrace();
        }

    }

    public void delete() {
        allrealm.remove(this);
        owner.setOwned(null);
        List<RealmPlayer> toRemove = new ArrayList<>();
        toRemove.addAll(realmmembers);
        for (RealmPlayer rp : toRemove)
            kickPlayer(rp);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (cuboid.containsLocation(player.getLocation())) {
                new ConfigFiles().sendToSpawn(player);
            }
        }
        new RealmConfig().delete(this);
        new RealmConfig().removeVotes(this);
            for (Block block : cuboid.getBlocks()) {
                block.setType(Material.AIR);
            }
            for (Entity entity : theme.getSpawn().getWorld().getEntities()) {
                if (!(entity instanceof Player) && cuboid.containsLocation(entity.getLocation())) {
                    entity.remove();
                }
            }
    }
    public boolean isInRealm(Location loc){
        return cuboid.containsLocation(loc);
    }
    public void demote(RealmPlayer player) {
        player.rankbyrealm.remove(this);
    }

    public ArrayList<RealmPlayer> getBanned() {
        return banned;
    }

    public ArrayList<RealmPlayer> getRealmMembers() {
        return realmmembers;
    }

    public RealmPlayer getOwner() {
        return owner;
    }

    public boolean getPrivacy() {
        return privacy;
    }

    public void setPrivacy(boolean b) {
        privacy = b;
        new RealmConfig().setPrivacy(this);
        if (b) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                RealmPlayer realmPlayer = RealmPlayer.getPlayer(p.getUniqueId().toString());
                if (this.cuboid.containsLocation(p.getLocation()) && !this.getRealmMembers().contains(realmPlayer)) {
                    p.teleport(ConfigFiles.getSpawn());
                }
            }
        }
    }

    public String getPerk() {
        return perk;
    }

    public void setPerk(String s) {
        perk = s;
        new RealmConfig().setPerk(this);
    }

    public RealmLevel getLevel() {
        return level;
    }

    public void setLevel(int i) {
        level = Main.getInstance().getRealmLevel().getLevelByRank(i, getRank());
    }

    public void addVote() {
        vote++;
        new RealmConfig().updateVote(this);
    }

    public static Realm getRealmFromLocation(Location location) {
        for (Realm r : allrealm) {
            if (r.cuboid.containsLocation(location)) {
                return r;
            }
        }
        return null;
    }

    public String getPrivacyString() {
        if (this.getPrivacy())
            return "Private";
        else
            return "Public";
    }

    public void setOwner(RealmPlayer owner) {
        this.owner = owner;
    }

    public CuboidUtils getCuboid() {
        return cuboid;
    }

    public int getVote() {
        return vote;
    }

    public int getMoney(){
        return money;
    }

    public void addMoney(int money){
        this.money+= money;
        new RealmConfig().setMoney(this);
    }

    private void useless() {
        ArrayList<String> strs = new ArrayList<>();
        strs.forEach(System.out::println);
    }
}

