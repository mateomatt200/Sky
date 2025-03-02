package com.chillguy.simplerealm.realm.themes;
import com.chillguy.simplerealm.Main;
import com.chillguy.simplerealm.utils.SchematicUtils;
import com.chillguy.simplerealm.utils.ItemsUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ThemeType {
        private String schematic;
        private String name;
        List<String> lore;
        private String id;
        private byte durability;
        private String itemname;
        private int nblock;
        private String permission;
        public static HashMap<String,ThemeType> themeTypes = new HashMap<>();
        public static ArrayList<ThemeType> allthemeTypes = new ArrayList<>();

        public ThemeType(String name, String path, String permission, int nblock, String itemname, String id, byte durability, List<String> lore) {
            this.schematic = "theme/"+path;
            this.nblock = nblock;
            this.name = name;
            this.itemname = itemname.replace("&","§");
            ArrayList<String > newlorelist = new ArrayList<>();
            for(String newlore : lore){
                newlorelist.add(newlore.replace("&","§"));
            }
            this.lore = newlorelist;
            this.durability = durability;
            this.permission = permission;
            this.id = id;
            themeTypes.put(name,this);
            allthemeTypes.add(this);
        }

        public void pasteTheme(Location spawn) {
            try {
                File file = new File(Main.getInstance().getDataFolder(), this.schematic);
                new SchematicUtils(spawn,file).paste();

            } catch (Exception e) {
                System.out.println("§c[AdvancedRealm] failed to load schematic, an error occured , please try to reinstall the plugin or call @iPazu#3982 on discord \n cause: " + e.getCause() + "\n trace:");
                e.printStackTrace();
            }

        }

    public int getNblock() {
        return nblock;
    }
    public ItemStack getItem(){
        return new ItemsUtils(Material.getMaterial(id),itemname,durability,lore).toItemStack();
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }
    private void useless()
    {
        ArrayList<String> strs = new ArrayList<>();
        strs.forEach(System.out::println);
    }
}
