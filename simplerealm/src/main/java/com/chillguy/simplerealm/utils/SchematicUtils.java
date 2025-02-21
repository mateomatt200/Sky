package com.chillguy.simplerealm.utils;

import com.chillguy.simplerealm.Main;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SchematicUtils {
    private final Location location;
    private final File file;

    public SchematicUtils(Location location, File file) {
        this.location = location;
        this.file = file;
    }

    public void paste() throws Exception {
        if (Bukkit.getVersion().contains("1.13") || Bukkit.getVersion().contains("1.14") || Bukkit.getVersion().contains("1.15") ||
                Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17") || Bukkit.getVersion().contains("1.18") ||
                Bukkit.getVersion().contains("1.19") || Bukkit.getVersion().contains("1.20")) {
            pasteWithWorldEdit();
        } else {
            pasteLegacy(); // Legacy fallback if needed
        }
    }

    private void pasteLegacy() {
        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            try {
                ClipboardFormat format = ClipboardFormats.findByFile(file);
                if (format == null) {
                    throw new IOException("Unsupported schematic format!");
                }

                try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
                    Clipboard clipboard = reader.read();
                    World adaptedWorld = BukkitAdapter.adapt(location.getWorld());
                    BlockVector3 position = BlockVector3.at(location.getX(), location.getY(), location.getZ());

                    try (EditSession editSession = WorldEdit.getInstance().newEditSession(adaptedWorld)) {
                        ClipboardHolder holder = new ClipboardHolder(clipboard);
                        Operations.complete(holder.createPaste(editSession)
                                .to(position)
                                .ignoreAirBlocks(true)
                                .build());
                        editSession.flushSession();
                    } catch (WorldEditException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void pasteWithWorldEdit() throws IOException {
        pasteLegacy(); // Use the modern WorldEdit method
    }
}
