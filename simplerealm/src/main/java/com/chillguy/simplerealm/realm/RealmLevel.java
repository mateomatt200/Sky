package com.chillguy.simplerealm.realm;


import com.chillguy.simplerealm.Main;
import com.chillguy.simplerealm.utils.blocks.BlockLimitation;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;

import java.util.*;

public class RealmLevel {
    // Organización por rango y nivel
    private Main plugin;

    private int price;
    private int bordersize;
    private int maxplayer;
    private int number;
    private String rank;
    private int width;
    private int adjustment;
    private int move_border;

    List<BlockLimitation> limitations;


    public RealmLevel(Main plugin) {
        this.plugin = plugin;
    }

    // Constructor para instanciar niveles específicos
    public RealmLevel(String rank, int number, int price, int bordersize, int maxplayer, int width, List<BlockLimitation> materials, int adjustment
                    , int move_border ) {
        this.price = price;
        this.bordersize = bordersize;
        this.maxplayer = maxplayer;
        this.number = number;
        this.rank = rank;
        this.width = width;
        this.limitations = materials;
        this.adjustment = adjustment;
        this.move_border = move_border;

        Main.getInstance().getRealmMap().getRealmLevels().putIfAbsent(rank, new HashMap<>());
        Main.getInstance().getRealmMap().getRealmLevels().get(rank).put(number, this);
    }

    public int getMove_border() {
        return move_border;
    }

    public int getAdjustment() {
        return adjustment;
    }

    public List<BlockLimitation> getLimitations() {
        return limitations;
    }

    public String getRank() {
        return rank;
    }

    public int getWidth() {
        return width;
    }

    public int getPrice() {
        return price;
    }


    public int getBordersize() {
        return bordersize;
    }

    public int getMaxplayer() {
        return maxplayer;
    }

    public int getNumber() {
        return number;
    }

    public int getMaxLevel(String rank){
        return Main.getInstance().getRealmMap().getRealmLevels().get(rank).size();
    }

    public HashMap<Integer, RealmLevel> getRealmLevels(String rank) {
        return Main.getInstance().getRealmMap().getRealmLevels().get(rank);
    }

    public RealmLevel getRealmByRankAndLevel(String rank, int level) {
        return Main.getInstance().getRealmMap().getRealmLevels().get(rank).get(level);
    }


    public RealmLevel getLevelByRank(int level, String rank) {
        HashMap<Integer, RealmLevel> levelsByRank = Main.getInstance().getRealmMap().getRealmLevels().get(rank);
        if (levelsByRank != null) {
            return levelsByRank.get(level);
        }
        return null; // Devuelve null si el rango o nivel no existe
    }

    // Obtiene un nivel específico de un rango


    public RealmLevel getLevel(int level, UUID player) {
        String rank = getPlayerRank(player);
        HashMap<Integer, RealmLevel> levelsByRank = Main.getInstance().getRealmMap().getRealmLevels().get(rank);
        if (levelsByRank != null) {
            return levelsByRank.get(level);
        }
        return null; // Devuelve null si el rango o nivel no existe
    }

    public int getByRankLevel(String rank, int level) {
        HashMap<Integer, RealmLevel> levelsByRank = Main.getInstance().getRealmMap().getRealmLevels().get(rank);
        if (levelsByRank != null) {
            System.out.println("Level " + level);
            System.out.println("rank " + rank);
            for(RealmLevel levels : levelsByRank.values()){
                System.out.println("levels " + levels.getBordersize());
            }

            System.out.println("byRank " + levelsByRank.get(level).getBordersize());
            return levelsByRank.get(level).getBordersize();
        }
        return 0; // Devuelve null si el rango o nivel no existe
    }

    // Devuelve todos los niveles de un rango específico
    public HashMap<Integer, RealmLevel> getLevelsByRank(String rank) {
        return Main.getInstance().getRealmMap().getRealmLevels().get(rank);
    }

    public List<String> getPlayerRanks(UUID uuid){
        try {
            LuckPerms luckPerms = plugin.getLuckPerms();
            User user = luckPerms.getUserManager().getUser(uuid);
            if (user == null) {
                user = luckPerms.getUserManager().loadUser(uuid).join();
            }
            if (user != null) {
                QueryOptions queryOptions = luckPerms.getContextManager().getQueryOptions(user)
                        .orElse(QueryOptions.defaultContextualOptions());

                // Obtener grupos heredados del usuario
                var inheritedGroups = user.getInheritedGroups(queryOptions);

                List<String> ranks = new ArrayList<>();
                // Priorizar grupo VIP si existe
                for (var group : inheritedGroups) {
                    ranks.add(group.getName());
                }
                ranks.add("default");

                return ranks;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>(); // Valor por defecto si no se puede obtener el rango.
    }

    public String getPlayerRank(UUID uuid) {
        try {
            LuckPerms luckPerms = plugin.getLuckPerms();
            User user = luckPerms.getUserManager().getUser(uuid);
            if (user == null) {
                user = luckPerms.getUserManager().loadUser(uuid).join();
            }
            if (user != null) {
                QueryOptions queryOptions = luckPerms.getContextManager().getQueryOptions(user)
                        .orElse(QueryOptions.defaultContextualOptions());

                // Obtener grupos heredados del usuario
                var inheritedGroups = user.getInheritedGroups(queryOptions);

                // Priorizar grupo VIP si existe
                for (var group : inheritedGroups) {
                    if (group.getName().equalsIgnoreCase("vip")) {
                        return "vip";
                    }
                }

                // Si no hay grupo VIP, devolver el grupo por defecto
                return user.getPrimaryGroup();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Sin Rango"; // Valor por defecto si no se puede obtener el rango.
    }
}