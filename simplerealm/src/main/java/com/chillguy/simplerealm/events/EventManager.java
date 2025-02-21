package com.chillguy.simplerealm.events;


import com.chillguy.simplerealm.events.gen.OreEvent;
import com.chillguy.simplerealm.events.theme.ThemeEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class EventManager {
    Plugin plugin;

    public EventManager(Plugin plugin) {
        this.plugin = plugin;
        registerEvents();
    }

    public void registerEvents() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new ThemeEvent(), this.plugin);
        pm.registerEvents(new OreEvent(), this.plugin);
        pm.registerEvents(new JoinEvent(),this.plugin);
        pm.registerEvents(new InterractEvent(),this.plugin);
        pm.registerEvents(new PerkEvent(),this.plugin);
    }
}
