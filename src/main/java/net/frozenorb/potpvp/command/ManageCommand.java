package net.frozenorb.potpvp.command;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.frozenorb.potpvp.arena.menu.manageschematics.ManageSchematicsMenu;
import net.frozenorb.potpvp.kittype.menu.manage.ManageKitTypeMenu;
import net.frozenorb.potpvp.kittype.menu.select.SelectKitTypeMenu;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.menu.Button;
import net.frozenorb.potpvp.kt.menu.Menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public final class ManageCommand {

    @Command(names = {"manage"}, permission = "potpvp.admin")
    public static void manage(Player sender) {
        new ManageMenu().openMenu(sender);
    }

    public static class ManageMenu extends Menu {

        public ManageMenu() {
            super("Admin Management Menu");
        }

        @Override
        public Map<Integer, Button> getButtons(Player player) {
            return ImmutableMap.of(
                3, new ManageKitButton(),
                5, new ManageArenaButton()
            );
        }

    }

    private static class ManageKitButton extends Button {

        @Override
        public String getName(Player player) {
            return ChatColor.YELLOW + "Manage kit type definitions";
        }

        @Override
        public List<String> getDescription(Player player) {
            return ImmutableList.of();
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.DIAMOND_SWORD;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, InventoryView view) {
            player.closeInventory();

            new SelectKitTypeMenu((kitType) -> {
                player.closeInventory();
                new ManageKitTypeMenu(kitType).openMenu(player);
            }, false, "Manage Kit Type...").openMenu(player);
        }

    }

    private static class ManageArenaButton extends Button {

        @Override
        public String getName(Player player) {
            return ChatColor.YELLOW + "Manage the arena grid";
        }

        @Override
        public List<String> getDescription(Player player) {
            return ImmutableList.of();
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.IRON_PICKAXE;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, InventoryView view) {
            player.closeInventory();
            new ManageSchematicsMenu().openMenu(player);
        }

    }

}