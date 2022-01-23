package net.frozenorb.potpvp.follow.command;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.match.command.LeaveCommand;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class SilentFollowCommand {

    @Command(names = "silentfollow", permission = "potpvp.silent")
    public static void silentfollow(Player sender, @Param(name = "target") Player target) {
        sender.setMetadata("ModMode", new FixedMetadataValue(PotPvPND.getInstance(), true));
        sender.setMetadata("invisible", new FixedMetadataValue(PotPvPND.getInstance(), true));

        if (PotPvPND.getInstance().getPartyHandler().hasParty(sender)) {
            LeaveCommand.leave(sender);
        }

        FollowCommand.follow(sender, target);
    }

}
