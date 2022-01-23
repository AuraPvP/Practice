package net.frozenorb.potpvp.party.command;

import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.PotPvPLang;
import net.frozenorb.potpvp.kt.command.Command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public final class PartyHelpCommand {

    private static final List<String> HELP_MESSAGE = ImmutableList.of(
        ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + PotPvPLang.LONG_LINE,
        "§d§lParty Help",
        ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + PotPvPLang.LONG_LINE,
        "§cParty Commands:",
        "§f/party invite §7- §fInvite a player to join your party",
        "§f/party leave §7- §fLeave your current party",
        "§f/party accept [player] §7- §fAccept party invitation",
        "§f/party info [player] §7- §fView the roster of the party",
        "",
        "§cLeader Commands:",
        "§f/party kick <player> §7- §fKick a player from your party",
        "§f/party leader <player> §7- §fTransfer party leadership",
        "§f/party disband §7 - §fDisbands party",
        "§f/party lock §7 - §fLock party from others joining",
        "§f/party open §7 - §fOpen party to others joining",
        "§f/party password <password> §7 - §fSets party password",
        "",
        "§cOther Help:",
        "§fTo use §dparty chat§f, prefix your message with the §7'§d@§7' §fsign.",
        ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + PotPvPLang.LONG_LINE
    );

    @Command(names = {"party", "p", "t", "team", "f", "party help", "p help", "t help", "team help", "f help"}, permission = "")
    public static void party(Player sender) {
        HELP_MESSAGE.forEach(sender::sendMessage);
    }

}