package com.venned.simplerealm;

import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import com.venned.simplerealm.commands.Border;
import com.venned.simplerealm.events.EventManager;
import com.venned.simplerealm.utils.ARExpansion;
import com.venned.simplerealm.utils.ConfigFiles;
import net.ess3.api.MaxMoneyException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Main extends JavaPlugin {
    private static Main instance;

    private static final Logger log = Logger.getLogger("Minecraft");


    //public static Metrics metrics;
    public static Class<?> wrapperclass;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ARExpansion(this).register();
        }
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
        getCommand("border").setExecutor(new Border());
        getCommand("unclaim").setExecutor(new com.venned.simplerealm.commands.Unclaim());
        getCommand("claim").setExecutor(new com.venned.simplerealm.commands.Claim());
        getCommand("realm").setExecutor(new com.venned.simplerealm.commands.RealmCommand());
        getCommand("home").setExecutor(new com.venned.simplerealm.commands.Home());
        getCommand("visit").setExecutor(new com.venned.simplerealm.commands.Visit());
        getCommand("configrealm").setExecutor(new com.venned.simplerealm.commands.ConfigRealm());
        //pushMetrics();
    }

    @Override
    public void onDisable() {
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
            wrapperclass = Class.forName("com.venned.simplerealm.utils.SchematicWrapper");
            System.out.println("Clase SchematicWrapper cargada correctamente.");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se pudo encontrar la clase SchematicWrapper.");
            e.printStackTrace();
        }
    }
}
