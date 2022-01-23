package net.frozenorb.potpvp.tab;

import java.util.function.BiConsumer;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kt.tab.TabLayout;
import net.frozenorb.potpvp.kt.util.PlayerUtils;
import net.frozenorb.potpvp.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

final class HeaderLayoutProvider implements BiConsumer<Player, TabLayout> {

    @Override
    public void accept(Player player, TabLayout tabLayout) {
        header: {
            tabLayout.set(1, 0, CC.translate("&4&lNA Practice"));
            tabLayout.set(0, 1, CC.translate("&cOnline: &f" + Bukkit.getOnlinePlayers().size()));
            tabLayout.set(2, 1, ChatColor.GRAY + "&cFighting: &f" + PotPvPND.getInstance().getCache().getFightsCount());
            tabLayout.set(0, 20, ChatColor.GRAY + "" + ChatColor.ITALIC + "smoked.vip");
            tabLayout.set(1, 20, ChatColor.GRAY + "" + ChatColor.ITALIC + "store.smoked.vip");
            tabLayout.set(2, 20, ChatColor.GRAY + "" + ChatColor.ITALIC + "www.smoked.vip");
        }

        status: {
        }
    }

}
