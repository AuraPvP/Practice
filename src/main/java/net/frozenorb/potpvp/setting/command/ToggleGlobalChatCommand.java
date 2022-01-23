package net.frozenorb.potpvp.setting.command;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.setting.Setting;
import net.frozenorb.potpvp.setting.SettingHandler;
import net.frozenorb.potpvp.kt.command.Command;

import net.frozenorb.potpvp.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * /toggleglobalchat command, allows players to toggle {@link Setting#ENABLE_GLOBAL_CHAT} setting
 */
public final class ToggleGlobalChatCommand {

    @Command(names = {"toggleGlobalChat", "tgc", "togglechat"}, permission = "")
    public static void toggleGlobalChat(Player sender) {
        if (!Setting.ENABLE_GLOBAL_CHAT.canUpdate(sender)) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return;
        }

        SettingHandler settingHandler = PotPvPND.getInstance().getSettingHandler();
        boolean enabled = !settingHandler.getSetting(sender, Setting.ENABLE_GLOBAL_CHAT);

        settingHandler.updateSetting(sender, Setting.ENABLE_GLOBAL_CHAT, enabled);

        if (enabled) {
            sender.sendMessage(CC.translate("&fYou have &aenabled&f global chat"));
        } else {
            sender.sendMessage(CC.translate("&fYou have &cdisabled&f global chat"));
        }
    }

}