package net.frozenorb.potpvp.postmatchinv.menu;

import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kittype.HealingMethod;
import net.frozenorb.potpvp.kt.menu.Button;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

final class PostMatchHealsLeftButton extends Button {

    private final UUID player;
    private final HealingMethod healingMethod;
    private final int healsRemaining;
    private final int missedHeals;

    PostMatchHealsLeftButton(UUID player, HealingMethod healingMethod, int healsRemaining, int missedHeals) {
        this.player = player;
        this.healingMethod = healingMethod;
        this.healsRemaining = healsRemaining;
        this.missedHeals = missedHeals;
    }

    @Override
    public String getName(Player player) {
    return ChatColor.LIGHT_PURPLE.toString() + "Potions:";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            " " + ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD.toString() + "* " + "&fHealth Pots&7: &5" + healsRemaining + " Potion" + (healsRemaining == 1 ? "." : "s."),
            " " + ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD.toString() + "* " + "&fMissed Pots&7: &5" +  + missedHeals + " Potion" + (missedHeals == 1 ? "." : "s.")
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.POTION;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemStack item = super.getButtonItem(player);
        item.setDurability(healingMethod.getIconDurability());
        return item;
    }

    @Override
    public int getAmount(Player player) {
        return healsRemaining;
    }

}