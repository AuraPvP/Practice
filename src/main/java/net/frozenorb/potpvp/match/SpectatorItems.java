package net.frozenorb.potpvp.match;

import static net.frozenorb.potpvp.util.CC.DARK_GREEN;
import static net.frozenorb.potpvp.util.CC.GRAY;
import static net.frozenorb.potpvp.util.CC.GREEN;
import static net.frozenorb.potpvp.util.CC.PINK;
import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.RED;

import net.frozenorb.potpvp.kt.util.ItemUtils;
import net.frozenorb.potpvp.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class SpectatorItems {

    public static final ItemStack SHOW_SPECTATORS_ITEM = new ItemStack(Material.INK_SACK, 1, DyeColor.GRAY.getDyeData());
    public static final ItemStack HIDE_SPECTATORS_ITEM = new ItemStack(Material.INK_SACK, 1, DyeColor.LIME.getDyeData());

    public static final ItemStack VIEW_INVENTORY_ITEM = new ItemStack(Material.BOOK);

    // these items both do the same thing but we change the name if
    // clicking the item will reuslt in the player being removed
    // from their party. both serve the function of returning a player
    // to the lobby.
    // https://github.com/FrozenOrb/PotPvP-SI/issues/37
    public static final ItemStack RETURN_TO_LOBBY_ITEM = new ItemStack(Material.INK_SACK, 1, DyeColor.RED.getDyeData());
    public static final ItemStack LEAVE_PARTY_ITEM = new ItemStack(Material.INK_SACK, 1, DyeColor.RED.getDyeData());

    static {
        ItemUtils.setDisplayName(SHOW_SPECTATORS_ITEM, BLUE.toString() + BOLD + "» " + GRAY + BOLD + "Show Spectators" + BLUE.toString() + BOLD + " «");
        ItemUtils.setDisplayName(HIDE_SPECTATORS_ITEM, BLUE.toString() + BOLD + "» " + GREEN + BOLD + "Hide Spectators" + BLUE.toString() + BOLD + " «");

        ItemUtils.setDisplayName(VIEW_INVENTORY_ITEM, BLUE.toString() + BOLD + "» " + DARK_GREEN + BOLD + "View Inventory" + BLUE.toString() + BOLD + " «");

        ItemUtils.setDisplayName(RETURN_TO_LOBBY_ITEM, BLUE.toString() + BOLD + "» " + RED + BOLD + "Return to Spawn" + BLUE.toString() + BOLD + " «");
        ItemUtils.setDisplayName(LEAVE_PARTY_ITEM, BLUE.toString() + BOLD + "» " + RED + BOLD + "Leave Party" + BLUE.toString() + BOLD + " «");
    }

}