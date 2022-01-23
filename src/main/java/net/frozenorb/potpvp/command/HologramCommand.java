package net.frozenorb.potpvp.command;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.hologram.HologramHandler;
import net.frozenorb.potpvp.hologram.HologramMeta;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import net.frozenorb.potpvp.util.CC;
import org.bukkit.entity.Player;

public class HologramCommand {

    @Command(names = {"hologram create"}, permission = "potpvp.admin")
    public void execute(Player player, @Param(name = "type: (global/kittype)") String type, @Param(name = "name") String name) {
        if (type.equalsIgnoreCase("global")) {
            PotPvPND.getInstance().getHologramHandler().createGlobalHologram(player.getLocation(), name);
            player.sendMessage(CC.GREEN + "Hologram Created Successfully!");
        } else {
            KitType kitType = KitType.byId(type.toUpperCase());
            if (kitType == null) {
                player.sendMessage(CC.RED + "That kit does not exist");
                return;
            }
            PotPvPND.getInstance().getHologramHandler().createKitHologram(player.getLocation(), kitType, name);
            player.sendMessage(CC.GREEN + "Hologram Created Successfully!");
            return;
        }
        player.sendMessage(CC.translate("&cInvalid Type!"));

    }

    @Command(names = {"hologram delete", "hologram remove"}, permission = "potpvp.admin")
    public void execute(Player player, @Param(name = "name") String name) {
        HologramMeta hologram = HologramHandler.getByName(name);
        if (hologram == null) {
            player.sendMessage(CC.translate("&7That Hologram does not exist!"));
        }
        PotPvPND.getInstance().getHologramHandler().deleteHologram(name);
        player.sendMessage(CC.translate("&aDeleted!"));
    }

}
