package net.frozenorb.potpvp.party;

import net.frozenorb.potpvp.PotPvPND;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class PartyLang {


    private static final TextComponent INVITED_YOU_TO_JOIN = new TextComponent("You've been invited to join a party, click to accept");

    private static final TextComponent ACCEPT_BUTTON = new TextComponent("(Click)");

    static {
        INVITED_YOU_TO_JOIN.setColor(ChatColor.GREEN);

        HoverEvent.Action showText = HoverEvent.Action.SHOW_TEXT; // readability
        BaseComponent[] acceptTooltip = new ComponentBuilder("Click to join party").color(ChatColor.GREEN).create();

        ACCEPT_BUTTON.setColor(ChatColor.GRAY);
        ACCEPT_BUTTON.setHoverEvent(new HoverEvent(showText, acceptTooltip));
    }

    public static TextComponent inviteAcceptPrompt(Party party) {
        ClickEvent.Action runCommand = ClickEvent.Action.RUN_COMMAND;
        String partyLeader = PotPvPND.getInstance().getUuidCache().name(party.getLeader());

        // create copies via constructor (we're going to update their click event)
        TextComponent acceptButton = new TextComponent(ACCEPT_BUTTON);

        acceptButton.setClickEvent(new ClickEvent(runCommand, "/p join " + partyLeader));

        TextComponent builder = new TextComponent("");

        builder.addExtra(hoverablePartyName(party));
        builder.addExtra(INVITED_YOU_TO_JOIN);
        builder.addExtra(acceptButton);
        builder.addExtra(new TextComponent(" "));

        return builder;
    }

    public static TextComponent hoverablePartyName(Party party) {
        TextComponent previewComponent = new TextComponent();
        String leaderName = PotPvPND.getInstance().getUuidCache().name(party.getLeader());

        // only show an actual tooltip for parties with >= 2 members,
        // parties that (to the user) don't exist yet just show up as a name
        if (party.getMembers().size() > 1) {
            HoverEvent hoverEvent = hoverablePreviewTooltip(party);

            previewComponent.setText("[" + leaderName + "'s Party]");
            previewComponent.setHoverEvent(hoverEvent);
        } else {
            previewComponent.setText(leaderName);
        }

        previewComponent.setColor(ChatColor.BLUE);
        return previewComponent;
    }

    public static HoverEvent hoverablePreviewTooltip(Party party) {
        ComponentBuilder builder = new ComponentBuilder("Members (").color(ChatColor.BLUE);
        String size = "" + party.getMembers().size();

        builder.append(size).color(ChatColor.AQUA);
        builder.append("):").color(ChatColor.BLUE);

        for (String member : getMemberPreviewNames(party)) {
            builder.append("\n");
            builder.append(member);
        }

        HoverEvent.Action action = HoverEvent.Action.SHOW_TEXT;
        return new HoverEvent(action, builder.create());
    }

    // this method is probably named badly;
    // it puts min(partySize, 6) member display names into a set,
    // with a String indicating how many more members are present (if there are any)
    private static List<String> getMemberPreviewNames(Party party) {
        List<UUID> members = new ArrayList<>(party.getMembers());
        int partySize = members.size();
        List<String> displayNames = new ArrayList<>();

        for (int i = 0; i < Math.min(partySize, 6); i++) {
            UUID member = members.remove(0);
            String suffix = party.isLeader(member) ? "*" : "";

            displayNames.add(ChatColor.YELLOW + PotPvPND.getInstance().getUuidCache().name(member) + suffix);
        }

        if (!members.isEmpty()) {
            displayNames.add(ChatColor.GRAY + "+ " + members.size() + " more");
        }

        return displayNames;
    }

}