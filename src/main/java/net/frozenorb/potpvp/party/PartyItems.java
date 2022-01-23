package net.frozenorb.potpvp.party;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kt.util.ItemUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.experimental.UtilityClass;

import static org.bukkit.ChatColor.*;

@UtilityClass
public final class PartyItems {

    public static final Material ICON_TYPE = Material.NETHER_STAR;

    public static final ItemStack LEAVE_PARTY_ITEM = new ItemStack(Material.INK_SACK, 1, DyeColor.RED.getDyeData());
    public static final ItemStack START_TEAM_SPLIT_ITEM = new ItemStack(Material.DIAMOND_SWORD);
    public static final ItemStack START_FFA_ITEM = new ItemStack(Material.GOLD_SWORD);
    public static final ItemStack OTHER_PARTIES_ITEM = new ItemStack(Material.SKULL_ITEM);

    static {
        ItemUtils.setDisplayName(LEAVE_PARTY_ITEM, BLUE.toString() + BOLD + "» " + RED + BOLD + "Leave Party" + BLUE.toString() + BOLD + " «");
        ItemUtils.setDisplayName(START_TEAM_SPLIT_ITEM, BLUE.toString() + BOLD + "» " + YELLOW + BOLD + "Start Team Split" + BLUE.toString() + BOLD + " «");
        ItemUtils.setDisplayName(START_FFA_ITEM, BLUE.toString() + BOLD + "» " + YELLOW + BOLD + "Start Party FFA" + BLUE.toString() + BOLD + " «");
        ItemUtils.setDisplayName(OTHER_PARTIES_ITEM, BLUE.toString() + BOLD + "» " + GREEN + BOLD + "Other Parties" + BLUE.toString() + BOLD + " «");
    }

    public static ItemStack icon(Party party) {
        ItemStack item = new ItemStack(ICON_TYPE);

        String leaderName = PotPvPND.getInstance().getUuidCache().name(party.getLeader());
        String displayName = BLUE.toString() + BOLD + "» " + AQUA.toString() + BOLD + "Party Information" + BLUE.toString() + BOLD + " «";

        ItemUtils.setDisplayName(item, displayName);
        return item;
    }

}
