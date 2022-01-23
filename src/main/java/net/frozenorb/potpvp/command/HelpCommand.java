package net.frozenorb.potpvp.command;

import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.PotPvPLang;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.match.MatchHandler;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Generic /help command, changes message sent based on if sender is playing in
 * or spectating a match.
 */
public final class HelpCommand {

    private static final List<String> HELP_MESSAGE_HEADER = ImmutableList.of(
        ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + PotPvPLang.LONG_LINE,
        "§6§lGeneral Help",
        ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + PotPvPLang.LONG_LINE,
        ""
    );

    private static final List<String> HELP_MESSAGE_LOBBY = ImmutableList.of(
        "§6Match:",
        "§f/duel <player> §7- Challenge a player to a duel",
        "§f/party invite <player> §7- Invite a player to a party",
        "§f/spectate <player> §7- Invite a player to a party",
        "§f/report <player> <reason> §7- Report a player for violating the rules",
        "§f/request <message> §7- Request assistance from a staff member",
        "",
        "§6Other:",
        "§f/party help §7- §fInformation on party commands",
        "§f/report <player> <reason> §7- §fReport a player for violating the rules",
        "§f/request <message> §7- §fRequest assistance from a staff member",
        ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + PotPvPLang.LONG_LINE
    );



    @Command(names = {"help", "?", "halp", "helpme"}, permission = "")
    public static void help(Player sender) {
        MatchHandler matchHandler = PotPvPND.getInstance().getMatchHandler();

        HELP_MESSAGE_HEADER.forEach(sender::sendMessage);
        HELP_MESSAGE_LOBBY.forEach(sender::sendMessage);
    }

}
