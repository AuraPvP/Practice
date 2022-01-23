package net.frozenorb.potpvp.rematch;

import net.frozenorb.potpvp.kt.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.experimental.UtilityClass;

import static net.frozenorb.potpvp.util.CC.DARK_PURPLE;
import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.GREEN;

@UtilityClass
public final class RematchItems {

    public static final ItemStack REQUEST_REMATCH_ITEM = new ItemStack(Material.DIAMOND);
    public static final ItemStack SENT_REMATCH_ITEM = new ItemStack(Material.EMERALD);
    public static final ItemStack ACCEPT_REMATCH_ITEM = new ItemStack(Material.EMERALD);

    static {
        ItemUtils.setDisplayName(REQUEST_REMATCH_ITEM, BLUE.toString() + BOLD + "» " + DARK_PURPLE + BOLD + "Request Rematch" + BLUE.toString() + BOLD + " «");
        ItemUtils.setDisplayName(SENT_REMATCH_ITEM, BLUE.toString() + BOLD + "» " + GREEN + BOLD + "Sent Rematch" + BLUE.toString() + BOLD + " «");
        ItemUtils.setDisplayName(ACCEPT_REMATCH_ITEM, BLUE.toString() + BOLD + "» " + GREEN + BOLD + "Accept Rematch" + BLUE.toString() + BOLD + " «");
    }

}