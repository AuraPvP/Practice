package net.frozenorb.potpvp.listener;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public final class ChatFormatListener implements Listener {
    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String prefix = CC.translate(PotPvPND.chat.getPlayerPrefix(player));
        event.setFormat(prefix + " " + "%s" + ChatColor.RESET + "" + ChatColor.GRAY + " » " + ChatColor.RESET + "%s");
    }
}