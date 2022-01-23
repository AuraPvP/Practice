package net.frozenorb.potpvp.setting.command;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.setting.Setting;
import net.frozenorb.potpvp.setting.SettingHandler;
import net.frozenorb.potpvp.kt.command.Command;

import net.frozenorb.potpvp.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * /night command, allows players to toggle {@link Setting#NIGHT_MODE} setting
 */
public final class NightCommand {

    @Command(names = { "night", "nightMode" }, permission = "")
    public static void night(Player sender) {
        if (!Setting.NIGHT_MODE.canUpdate(sender)) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return;
        }

        SettingHandler settingHandler = PotPvPND.getInstance().getSettingHandler();
        boolean enabled = !settingHandler.getSetting(sender, Setting.NIGHT_MODE);

        settingHandler.updateSetting(sender, Setting.NIGHT_MODE, enabled);

        if (enabled) {
            sender.sendMessage(CC.translate("&fYou have updated night mode to &atrue"));
        } else {
            sender.sendMessage(ChatColor.RED + "&fYou have updated night mode to &cfalse");
        }
    }

}