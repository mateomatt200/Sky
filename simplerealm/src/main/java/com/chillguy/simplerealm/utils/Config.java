package com.chillguy.simplerealm.utils;

import com.chillguy.simplerealm.Main;
import com.chillguy.simplerealm.realm.RealmLevel;
import com.chillguy.simplerealm.realm.RealmPlayer;
import com.chillguy.simplerealm.realm.gen.GenChances;
import com.chillguy.simplerealm.realm.gen.GenLevel;
import com.chillguy.simplerealm.realm.Realm;
import com.chillguy.simplerealm.utils.blocks.BlockLimitation;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public enum Config {
    CONFIG(Main.getInstance().getDataFolder(),"config.yml",true),
    REALM(Main.getInstance().getDataFolder(),"realm.yml",false),
    ASPECT(Main.getInstance().getDataFolder(),"aspect.yml",true),
    UPGRADES(Main.getInstance().getDataFolder(),"upgrades.yml",true),
    GEN(Main.getInstance().getDataFolder(),"gen.yml",true);


    private final String name;
    private YamlConfiguration config;
    private final File folder;
    private final boolean copydefault;

    Config(File folder, String name,boolean copydefault) {
        this.name = name;
        this.folder = folder;
        this.copydefault = copydefault;
    }

    public String getFileName() {
        return name;
    }

    public YamlConfiguration getConfig() {
        return config;
    }

    public boolean getCopyDefault(){
        return copydefault;
    }

    public File getFolder(){
        return folder;
    }

    public static Material getMaterial(String name){
        return Material.getMaterial(name.toUpperCase());
    }

    public static String getStringWithReplacementPlayer(String oldstring, Realm r, RealmPlayer rp){
        String newstring = getStringWithReplacementRealm(oldstring,r);
        newstring = pushColor(newstring);
        newstring = newstring.replace("%player_name%",rp.getName());
        newstring = newstring.replace("%targeted_player%",rp.getName());

        if(r.getRealmMembers().contains(rp))
        newstring = newstring.replace("%player_rank%",rp.getRankByRealm(r).toString());
        return newstring;
    }

    public static String getStringWithReplacementRealm(String oldstring, Realm r){
        String newstring = oldstring;
        newstring = pushColor(newstring);
        newstring = newstring.replace("%realm_name%",r.getOwner().getName());
        newstring = newstring.replace("%realm_privacy%",r.getPrivacyString());
        newstring = newstring.replace("%realm_bordersize%",r.getLevel().getBordersize()+"");
        newstring = newstring.replace("%realm_widthsize%", r.getLevel().getWidth() + "");
        newstring = newstring.replace("%realm_width%", r.getLevel().getWidth() +"");
        newstring = newstring.replace("%realm_maxplayer%",r.getLevel().getMaxplayer()+"");
        newstring = newstring.replace("%realm_money%",r.getMoney()+"");
        newstring = newstring.replace("%realm_gen_level%", r.getGenLevel().getNivel() + "");
        newstring = newstring.replace("%realm_rank%", r.getRank().toUpperCase());

        GenLevel nextLevelGen = Main.getInstance().getRealmMap().getGenLevels().get(r.getGenLevel().getNivel() + 1);
        RealmLevel nextlevel = Main.getInstance().getRealmLevel().getLevelByRank(r.getLevel().getNumber() + 1, r.getRank());
        if(nextlevel != null){
            newstring = newstring.replace("%realm_nextwidth%", nextlevel.getWidth() +"");
            newstring = newstring.replace("%realm_nextbordersize%",nextlevel.getBordersize()+"");
            newstring = newstring.replace("%realm_nextwidthsize%", nextlevel.getWidth() + "");
            newstring = newstring.replace("%realm_nextmaxplayer%",nextlevel.getMaxplayer()+"");
            newstring = newstring.replace("%realm_nextlevelcost%",nextlevel.getPrice()+"");
        } else {
            newstring = newstring.replace("%realm_nextbordersize%","MAX");
            newstring = newstring.replace("%realm_nextmaxplayer%","MAX");
            newstring = newstring.replace("%realm_nextlevelcost%","MAX");
        }

        if(nextLevelGen != null){
            newstring = newstring.replace("%realm_nextprice%", nextLevelGen.getPrice() + "");
        }

        return newstring;
    }

    public static String pushColor(String oldstring){
        String newstring = oldstring;
        newstring = newstring.replace("&","§");
        return newstring;
    }
    public static List<String> getListWithReplacementPlayer(List<String> oldlist, Realm r,RealmPlayer rp){
        List<String> newlist = new ArrayList<>();
        for(String s : oldlist){
            newlist.add(getStringWithReplacementPlayer(s,r,rp));
        }
        return newlist;
    }
    public static List<String> getListWithReplacementRealm(List<String> oldlist, Realm r){
        List<String> newlist = new ArrayList<>();
        for(String s : oldlist){
            if(s.contains("%realm_list_limits%")){
                newlist.addAll(getRealmLimitsAsString(r));
                continue;
            } else if(s.contains("%realm_list_nextlimits%")){
                newlist.addAll(getRealmLimitsAsStringNext(r));
                continue;
            } else if(s.contains("%realm_gen_chance_current%")){
                newlist.addAll(getRealmGenAsString(r));
                continue;
            } else if(s.contains("%realm_gen_chance_next%")){
                newlist.addAll(getRealmNextGenAsString(r));
                continue;
            }
            newlist.add(getStringWithReplacementRealm(s,r));
        }
        return newlist;
    }


    private static List<String> getRealmLimitsAsStringNext(Realm r) {
        RealmLevel nextlevel = Main.getInstance().getRealmLevel().getLevelByRank(r.getLevel().getNumber() + 1, r.getRank());
        if(nextlevel != null) {
            List<BlockLimitation> blockLimits = nextlevel.getLimitations(); // Supongamos que este método devuelve los límites
            List<String> newList = new ArrayList<>();


            for (BlockLimitation blockLimitation : blockLimits) {
                StringBuilder limits = new StringBuilder();
                limits.append("§8- " + blockLimitation.getBlock().name())
                        .append("§8: ")
                        .append("§a" + blockLimitation.getQuantity());
                newList.add(limits.toString());
            }

            return newList;
        }
        return new ArrayList<>();
    }

    private static List<String> getRealmNextGenAsString(Realm r) {

        GenLevel genLevel = Main.getInstance().getRealmMap().getGenLevels().get(r.getGenLevel().getNivel() + 1);
        if(genLevel != null) {
            List<GenChances> blockLimits = genLevel.getListBlocks(); // Supongamos que este método devuelve los límites
            List<String> newList = new ArrayList<>();


            for (GenChances blockLimitation : blockLimits) {
                StringBuilder limits = new StringBuilder();
                limits.append("§f" + blockLimitation.getMaterial().name())
                        .append(" Chance§8: ")
                        .append("§a" + blockLimitation.getChance());
                newList.add(limits.toString());
            }

            return newList;
        }
        return  new ArrayList<>();
    }


    private static List<String> getRealmGenAsString(Realm r) {
        List<GenChances> blockLimits = r.getGenLevel().getListBlocks(); // Supongamos que este método devuelve los límites
        List<String> newList = new ArrayList<>();


        for (GenChances blockLimitation : blockLimits) {
            StringBuilder limits = new StringBuilder();
            limits.append("§f" + blockLimitation.getMaterial().name())
                    .append(" Chance§8: ")
                    .append("§e" + blockLimitation.getChance());
            newList.add(limits.toString());
        }

        return newList;
    }


    private static List<String> getRealmLimitsAsString(Realm r) {
        List<BlockLimitation> blockLimits = r.getLevel().getLimitations(); // Supongamos que este método devuelve los límites
        List<String> newList = new ArrayList<>();


        for (BlockLimitation blockLimitation : blockLimits) {
            StringBuilder limits = new StringBuilder();
            limits.append("§8- " + blockLimitation.getBlock().name())
                    .append("§8: ")
                    .append("§e" + blockLimitation.getQuantity());
            newList.add(limits.toString());
        }

        return newList;
    }

    public static int getCollumFromInt(int i){
      int collum = i -9*getRowFromInt(i);
      return collum;
    }
    public static int getRowFromInt(int i){
       int row = i/9;
       return row;
    }
    public File getFile(){
        return new File(getFolder(),getFileName());
    }
    public void setConfig(YamlConfiguration config) {
        this.config = config;
    }
}
