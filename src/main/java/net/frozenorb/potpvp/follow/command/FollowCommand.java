package net.frozenorb.potpvp.follow.command;

import com.qrakn.morpheus.game.Game;
import com.qrakn.morpheus.game.GameQueue;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.follow.FollowHandler;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.setting.Setting;
import net.frozenorb.potpvp.setting.SettingHandler;
import net.frozenorb.potpvp.validation.PotPvPValidation;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class FollowCommand {

    @Command(names={"follow"}, permission="")
    public static void follow(Player sender, @Param(name="target") Player target) {
        if (!PotPvPValidation.canFollowSomeone(sender)) {
            return;
        }

        FollowHandler followHandler = PotPvPND.getInstance().getFollowHandler();
        SettingHandler settingHandler = PotPvPND.getInstance().getSettingHandler();
        MatchHandler matchHandler = PotPvPND.getInstance().getMatchHandler();

        if (sender == target) {
            sender.sendMessage(ChatColor.RED + "No, you can't follow yourself.");
            return;
        } else if (!settingHandler.getSetting(target, Setting.ALLOW_SPECTATORS)) {
            if (sender.isOp()) {
                sender.sendMessage(ChatColor.RED + "Bypassing " + target.getName() + "'s no spectators preference...");
            } else {
                sender.sendMessage(ChatColor.RED + target.getName() + " doesn't allow spectators at the moment.");
                return;
            }
        }

        Game game = GameQueue.INSTANCE.getCurrentGame(target);
        if (game != null && game.getPlayers().contains(target)) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is playing an event!");
            return;
        }

        followHandler.getFollowing(sender).ifPresent(fo -> UnfollowCommand.unfollow(sender));

        if (matchHandler.isSpectatingMatch(sender)) {
            matchHandler.getMatchSpectating(sender).removeSpectator(sender);
        }

        followHandler.startFollowing(sender, target);
    }

}