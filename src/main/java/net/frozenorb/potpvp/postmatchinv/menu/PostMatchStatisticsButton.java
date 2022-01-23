package net.frozenorb.potpvp.postmatchinv.menu;

import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.kt.menu.Button;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

final class PostMatchStatisticsButton extends Button {

    private final int totalHits;
    private final int longestCombo;

    PostMatchStatisticsButton(int totalHits, int longestCombo) {
        this.totalHits = totalHits;
        this.longestCombo = longestCombo;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.YELLOW + "" + ChatColor.BOLD + "Stats:";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(ChatColor.GOLD + ChatColor.BOLD.toString() + " * " + ChatColor.WHITE + "Longest Combo" + ChatColor.GRAY.toString() + ": " + ChatColor.YELLOW + longestCombo + " Hit" + (longestCombo == 1 ? "." : "s."), ChatColor.GOLD + ChatColor.BOLD.toString() + " * " + ChatColor.WHITE + "Total Hits" + ChatColor.GRAY.toString() + ": " + ChatColor.YELLOW + totalHits + " Hit" + (totalHits == 1 ? "." : "s."));
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.CAKE;
    }

    @Override
    public int getAmount(Player player) {
        return 1;
    }

}