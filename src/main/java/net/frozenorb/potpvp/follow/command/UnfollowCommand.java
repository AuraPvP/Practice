package net.frozenorb.potpvp.follow.command;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.follow.FollowHandler;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.kt.command.Command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class UnfollowCommand {

    @Command(names={"unfollow"}, permission="")
    public static void unfollow(Player sender) {
        FollowHandler followHandler = PotPvPND.getInstance().getFollowHandler();
        MatchHandler matchHandler = PotPvPND.getInstance().getMatchHandler();

        if (!followHandler.getFollowing(sender).isPresent()) {
            sender.sendMessage(ChatColor.RED + "You're not following anybody.");
            return;
        }

        Match spectating = matchHandler.getMatchSpectating(sender);

        if (spectating != null) {
            spectating.removeSpectator(sender);
        }

        followHandler.stopFollowing(sender);
    }

}