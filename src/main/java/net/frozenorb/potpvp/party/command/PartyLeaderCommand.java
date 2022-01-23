package net.frozenorb.potpvp.party.command;

import net.frozenorb.potpvp.PotPvPLang;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class PartyLeaderCommand {

    @Command(names = {"party leader", "p leader", "t leader", "team leader", "leader", "f leader"}, permission = "")
    public static void partyLeader(Player sender, @Param(name = "player") Player target) {
        Party party = PotPvPND.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(PotPvPLang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PotPvPLang.NOT_LEADER_OF_PARTY);
        } else if (!party.isMember(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + target.getName() + " isn't in your party.");
        } else if (sender == target) {
            sender.sendMessage(ChatColor.RED + "You cannot promote yourself to the leader of your own party.");
        } else {
            party.setLeader(target);
        }
    }

}