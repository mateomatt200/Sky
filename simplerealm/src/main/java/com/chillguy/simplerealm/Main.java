package com.chillguy.simplerealm;

import com.chillguy.simplerealm.commands.*;
import com.chillguy.simplerealm.events.EventManager;
import com.chillguy.simplerealm.realm.RealmLevel;
import com.chillguy.simplerealm.utils.RealmMap;
import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import com.chillguy.simplerealm.commands.Border;
import com.chillguy.simplerealm.commands.ReloadCommand;
import com.chillguy.simplerealm.realm.Realm;
import com.chillguy.simplerealm.realm.RealmPlayer;
import com.chillguy.simplerealm.task.BorderTask;
import com.chillguy.simplerealm.utils.ARExpansion;
import com.chillguy.simplerealm.utils.ConfigFiles;
import net.ess3.api.MaxMoneyException;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.logging.Logger;

public class Main extends JavaPlugin {
    private static Main instance;

    private static final Logger log = Logger.getLogger("Minecraft");


    //public static Metrics metrics;
    public static Class<?> wrapperclass;

    LuckPerms luckPerms;
    RealmLevel realmLevel;
    RealmMap realmMap;
    BukkitTask task;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        realmMap = new RealmMap();
        realmLevel = new RealmLevel(this);


        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ARExpansion(this).register();
        }
        luckPerms = LuckPermsProvider.get();

        new ConfigFiles().init();
        new EventManager(this);
        //Setup vault
        /*
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

         */


        loadWrapperClass();
        getCommand("simplereload").setExecutor(new ReloadCommand());
        getCommand("border").setExecutor(new Border());
        getCommand("unclaim").setExecutor(new Unclaim());
        getCommand("claim").setExecutor(new Claim());
        getCommand("realm").setExecutor(new RealmCommand());
        getCommand("home").setExecutor(new Home());
        getCommand("visit").setExecutor(new Visit());
        getCommand("configrealm").setExecutor(new ConfigRealm());


        task = new BorderTask().runTaskTimer(this, 0, 1L);
        //pushMetrics();
    }

    @Override
    public void onDisable() {
    }

    public void reloadServer(){
        System.out.println("Reload 1");
        task.cancel();

        System.out.println("Reload");
        RealmPlayer.reloadAll();
        Realm.deleteAll();
        realmMap = new RealmMap();
        realmLevel = new RealmLevel(this);
        new ConfigFiles().init();
        task = new BorderTask().runTaskTimer(this, 0, 1L);
    }

    public RealmLevel getRealmLevel() {
        return realmLevel;
    }

    public RealmMap getRealmMap() {
        return realmMap;
    }

    public void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            System.out.println("no es null");
            return;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            System.out.println("rsp es null");
            return;
        }
       // economy = rsp.getProvider();
    }

    public int getBalance(Player player) throws UserDoesNotExistException {
        return (int) Economy.getMoney(player.getName());
    }

    public void withDraw(Player player, int money) throws UserDoesNotExistException, MaxMoneyException, NoLoanPermittedException {
        Economy.setMoney(player.getName(), getBalance(player) - money);
    }

    /*
    public static Metrics getMetrics() {
        return metrics;
    }
    /*

    private void pushMetrics() {
        metrics = new Metrics(this);
        metrics.addCustomChart(new Metrics.SingleLineChart("realms_created", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return Realm.allrealm.size();
            }
        }));
        System.out.println("[AdvancedRealm] Metrics successfully pushed (" + Realm.allrealm.size() + " realms)");
    }

     */

    public void loadWrapperClass() {
        try {
            wrapperclass = Class.forName("com.chillguy.simplerealm.utils.SchematicWrapper");
            System.out.println("Clase SchematicWrapper cargada correctamente.");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se pudo encontrar la clase SchematicWrapper.");
            e.printStackTrace();
        }
    }

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }
}
