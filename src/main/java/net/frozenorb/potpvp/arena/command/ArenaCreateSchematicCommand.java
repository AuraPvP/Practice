package net.frozenorb.potpvp.arena.command;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.arena.ArenaHandler;
import net.frozenorb.potpvp.arena.ArenaSchematic;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;

public final class ArenaCreateSchematicCommand {

    @Command(names = { "arena createSchematic" }, permission = "op")
    public static void arenaCreateSchematic(Player sender, @Param(name="schematic") String schematicName) {
        ArenaHandler arenaHandler = PotPvPND.getInstance().getArenaHandler();

        if (arenaHandler.getSchematic(schematicName) != null) {
            sender.sendMessage(ChatColor.RED + "Schematic " + schematicName + " already exists");
            return;
        }

        ArenaSchematic schematic = new ArenaSchematic(schematicName);
        File schemFile = schematic.getSchematicFile();

        if (!schemFile.exists()) {
            sender.sendMessage(ChatColor.RED + "No file for " + schematicName + " found. (" + schemFile.getPath() + ")");
            return;
        }

        arenaHandler.registerSchematic(schematic);

        try {
            schematic.pasteModelArena();
            arenaHandler.saveSchematics();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        sender.sendMessage(ChatColor.GREEN + "Schematic created.");
    }

}