package net.frozenorb.potpvp.setting;

import com.google.common.collect.ImmutableList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Setting {

    SHOW_SCOREBOARD(
        ChatColor.RED + "Scoreboard",
        ImmutableList.of(
            ChatColor.GREEN + "If enabled, you will be able to see the scoreboard"
        ),
        Material.ITEM_FRAME,
        ChatColor.GREEN + "Enabled",
        ChatColor.RED + "Disabled",
        true,
        null // no permission required
    ),
    SHOW_SPECTATOR_JOIN_MESSAGES(
        ChatColor.RED + "Spectator Message",
        ImmutableList.of(
            ChatColor.GREEN + "If enabled, you will receive a message whenever someone",
            ChatColor.GREEN + "uses the /spectate command to spectate your match"

        ),
        Material.BONE,
        ChatColor.GREEN + "Enabled",
        ChatColor.RED + "Disabled",
        true,
        null // no permission required
    ),
    VIEW_OTHER_SPECTATORS(
        ChatColor.RED + "Spectator View",
        ImmutableList.of(
            ChatColor.GREEN + "If enabled, you will be able to see other spectators",
            ChatColor.GREEN + "viewing the same match as you"
        ),
        Material.GLASS_BOTTLE,
        ChatColor.GREEN + "Enabled",
        ChatColor.RED + "Disabled",
        true,
        null // no permission required
    ),
    ALLOW_SPECTATORS(
            ChatColor.RED + "Spectators",
            ImmutableList.of(
                    ChatColor.GREEN + "If enabled, players can spectate your",
                    ChatColor.GREEN + "matches with /spectate."
            ),
            Material.REDSTONE_TORCH_ON,
            ChatColor.GREEN + "Enabled",
            ChatColor.RED + "Disabled",
            true,
            null // no permission required
    ),
    RECEIVE_DUELS(
        ChatColor.RED + "Duels",
        ImmutableList.of(
            ChatColor.GREEN + "If enabled, you will be able to receive",
            ChatColor.GREEN + "duels from other players or parties."
        ),
        Material.FIRE,
        ChatColor.GREEN + "Enabled",
        ChatColor.RED + "Disabled",
        true,
        "null"
    ),
    VIEW_OTHERS_LIGHTNING(
        ChatColor.RED + "Death Lightning",
        ImmutableList.of(
            ChatColor.GREEN + "If enabled, lightning will be visible",
            ChatColor.GREEN + "when other players die."
        ),
        Material.TORCH,
        ChatColor.GREEN + "Enabled",
        ChatColor.RED + "Disabled",
        true,
        null // no permission required
    ),
    NIGHT_MODE(
        ChatColor.RED + "Night Mode",
        ImmutableList.of(
            ChatColor.GREEN + "If enabled, your player time will be",
            ChatColor.GREEN + "changed to night time."
        ),
        Material.EYE_OF_ENDER,
        ChatColor.GREEN + "Enabled",
        ChatColor.RED + "Disabled",
        false,
        null // no permission required
    ),
    ENABLE_GLOBAL_CHAT(
        ChatColor.RED + "Global Chat",
        ImmutableList.of(
            ChatColor.GREEN + "If enabled, you will see messages",
            ChatColor.GREEN + "sent in the global chat channel."
        ),
        Material.BOOK_AND_QUILL,
        ChatColor.GREEN + "Enabled",
        ChatColor.RED + "Disabled",
        true,
        null // no permission required
    );

    /**
     * Friendly (colored) display name for this setting
     */
    @Getter private final String name;

    /**
     * Friendly (colored) description for this setting
     */
    @Getter private final List<String> description;

    /**
     * Material to be used when rendering an icon for this setting
     * @see net.frozenorb.potpvp.setting.menu.SettingButton
     */
    @Getter private final Material icon;

    /**
     * Text to be shown when rendering an icon for this setting, while enabled
     * @see net.frozenorb.potpvp.setting.menu.SettingButton
     */
    @Getter private final String enabledText;

    /**
     * Text to be shown when rendering an icon for this setting, while enabled
     * @see net.frozenorb.potpvp.setting.menu.SettingButton
     */
    @Getter private final String disabledText;

    /**
     * Default value for this setting, will be used for players who haven't
     * updated the setting and if a player's settings fail to load.
     */
    private final boolean defaultValue;

    /**
     * The permission required to be able to see and update this setting,
     * null means no permission is required to update/see.
     */
    private final String permission;

    // Using @Getter means the method would be 'isDefaultValue',
    // which doesn't correctly represent this variable.
    public boolean getDefaultValue() {
        return defaultValue;
    }

    public boolean canUpdate(Player player) {
        return permission == null || player.hasPermission(permission);
    }

}