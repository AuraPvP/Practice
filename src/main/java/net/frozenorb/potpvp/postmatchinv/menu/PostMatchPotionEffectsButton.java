package net.frozenorb.potpvp.postmatchinv.menu;

import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.kt.menu.Button;

import net.frozenorb.potpvp.kt.util.TimeUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.stream.Collectors;

final class PostMatchPotionEffectsButton extends Button {

    private final List<PotionEffect> effects;

    PostMatchPotionEffectsButton(List<PotionEffect> effects) {
        this.effects = ImmutableList.copyOf(effects);
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Potion Effects:";
    }

    @Override
    public List<String> getDescription(Player player) {
        if (!effects.isEmpty()) {
            return effects.stream()
                .map(effect -> ChatColor.YELLOW + ChatColor.BOLD.toString() + " * " + ChatColor.WHITE + formatEffectType(effect.getType()) + " " + (effect.getAmplifier() + 1) + ChatColor.GRAY + " (" + TimeUtils.formatIntoMMSS(effect.getDuration() / 20) + ChatColor.GRAY.toString() + ")").collect(Collectors.toList());
        } else {
            return ImmutableList.of(
            "",
            ChatColor.YELLOW + ChatColor.BOLD.toString() + " * " + ChatColor.WHITE + "No Effects"
            );
        }
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.BREWING_STAND_ITEM;
    }

    private String formatEffectType(PotionEffectType type) {
        switch (type.getName().toLowerCase()) {
            case "fire_resistance": return "Fire Resistance";
            case "increase_damage": return "Strength";
            case "damage_resistance": return "Resistance";
            case "speed": return "Speed";
            default: return StringUtils.capitalize(type.getName().toLowerCase());
        }
    }

}