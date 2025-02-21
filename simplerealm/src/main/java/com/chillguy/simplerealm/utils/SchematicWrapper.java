package com.chillguy.simplerealm.utils;

import com.boydti.fawe.FaweAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SchematicWrapper {

    public SchematicWrapper() {
    }

    /**
     * Paste a schematic using the modern WorldEdit API.
     *
     * @param file     The schematic file to paste.
     * @param location The location to paste the schematic.
     * @throws IOException If the file cannot be read or is not a valid schematic.
     */
    public void paste(File file, Location location) throws IOException {
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        if (format == null) {
            throw new IOException("Unsupported schematic format!");
        }

        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            Clipboard clipboard = reader.read();
            BlockVector3 position = BlockVector3.at(location.getX(), location.getY(), location.getZ());

            try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(location.getWorld()))) {
                ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);
                Operation operation = clipboardHolder
                        .createPaste(editSession)
                        .to(position)
                        .ignoreAirBlocks(true)
                        .build();
                Operations.complete(operation);
            } catch (WorldEditException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Paste a schematic for Minecraft 1.14 and above using the modern WorldEdit API.
     *
     * @param file     The schematic file to paste.
     * @param location The location to paste the schematic.
     * @throws Exception If the paste operation encounters an error.
     */
    public void paste14(File file, Location location) throws Exception {
        paste(file, location);
    }
}

