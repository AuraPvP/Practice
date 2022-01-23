package net.frozenorb.potpvp.hologram;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.util.CC;
import net.frozenorb.potpvp.util.LocationUtil;
import net.frozenorb.potpvp.util.TaskUtil;
import net.frozenorb.potpvp.util.config.BasicConfigurationFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

/**
 * @author Drizzy
 * Created at 4/20/2021
 */
public class HologramHandler {

    public static final List<HologramMeta> holograms = new ArrayList<>();
    public static final BasicConfigurationFile config = PotPvPND.getInstance().getHologramsConfig();

    /**
     * Create a Leaderboard Hologram according to KitType
     *
     * @param location The location of the hologram
     * @param kitType The Kittype of the Hologram for Leaderboards
     * @param name The name of the hologram
     */
    public void createKitHologram(Location location, KitType kitType, String name) {
        Hologram hologram = HologramsAPI.createHologram(PotPvPND.getInstance(), location);

        TaskUtil.runAsync(() -> {
            if (!location.getChunk().isLoaded())
                location.getChunk().load();
        });

        TaskUtil.runSync(() -> {
            hologram.getVisibilityManager().setVisibleByDefault(true);

            hologram.appendTextLine(CC.translate("&b&lTop 10"));
            hologram.appendTextLine(CC.translate("&b&lLeaderboards"));
            hologram.appendTextLine("");
            hologram.appendTextLine("&a⚉ &a&l" + kitType.getDisplayName() + " &a⚉");
            hologram.appendTextLine("");
            int counter = 1;
            for ( Map.Entry<String, Integer> entry : PotPvPND.getInstance().getEloHandler().topElo(kitType).entrySet()) {
                UUID uuid = PotPvPND.getInstance().getUuidCache().uuid(entry.getKey());
                String division = PotPvPND.getInstance().getDivisionHandler().getDivision(uuid);
                hologram.appendTextLine(CC.translate(CC.WHITE + counter + ". &a" + entry.getKey() + "&f - &b" + entry.getValue() + "&8(" + division+ "&8)"));
                counter++;
            }
        });
        HologramMeta meta = new HologramMeta(name, kitType.getDisplayName(), hologram);
        holograms.add(meta);
    }

    /**
     * Create a Global Leaderboards Hologram
     *
     * @param location The location of the hologram
     * @param name The name of the hologram
     */
    public void createGlobalHologram(Location location, String name) {
        Hologram hologram = HologramsAPI.createHologram(PotPvPND.getInstance(), location);

        TaskUtil.runAsync(() -> {
            if (!location.getChunk().isLoaded())
                location.getChunk().load();
        });

        TaskUtil.runSync(() -> {
            hologram.getVisibilityManager().setVisibleByDefault(true);

            hologram.appendTextLine(CC.translate("&b&lTop 10"));
            hologram.appendTextLine(CC.translate("&b&lLeaderboards"));
            hologram.appendTextLine("");
            hologram.appendTextLine("&a⚉ &a&lGlobal &a⚉");
            hologram.appendTextLine("");
            int counter = 1;
            for ( Map.Entry<String, Integer> entry : PotPvPND.getInstance().getEloHandler().topElo(null).entrySet()) {
                UUID uuid = PotPvPND.getInstance().getUuidCache().uuid(entry.getKey());
                String division = PotPvPND.getInstance().getDivisionHandler().getDivision(uuid);
                hologram.appendTextLine(CC.translate(CC.WHITE + counter + ". &a" + entry.getKey() + "&f - &b" + entry.getValue() + "&8(" + division+ "&8)"));
                counter++;
            }
        });
        HologramMeta meta = new HologramMeta(name, "global", hologram);
        holograms.add(meta);
    }

    /**
     * Get a Hologram by its UUID
     *
     * @param name The name of the Hologram
     * @return {@link HologramMeta}
     */
    public static HologramMeta getByName(String name) {
        for ( HologramMeta meta : holograms ) {
            if (meta.getName().equalsIgnoreCase(name)) {
                return meta;
            }
        }
        return null;
    }

    /**
     * Delete a Hologram using its Name
     *
     * @param name The Name of the hologram being deleted
     */
    public void deleteHologram(String name) {
        HologramMeta meta = getByName(name);

        if (meta == null) {
            throw new IllegalStateException("That Hologram does not exist!");
        }

        Hologram hologram = meta.getHologram();
        holograms.remove(meta);
        TaskUtil.runAsync(hologram::delete);
    }

    public void loadHolograms() {
        if(config.getConfiguration().getConfigurationSection("Holograms") == null) {
            return;
        }
        for ( String key : config.getConfiguration().getConfigurationSection("Holograms").getKeys(false) ) {
            String path="Holograms." + key;
            if (config.getString(path + "type").equalsIgnoreCase("global")) {
                this.createGlobalHologram(LocationUtil.deserialize(config.getString(path + "location")), key);
            } else {
                this.createKitHologram(LocationUtil.deserialize(config.getString(path + "location")), Objects.requireNonNull(KitType.byId(config.getString(key + "type").toUpperCase())), key);
            }
        }
        Bukkit.getConsoleSender().sendMessage(CC.translate("&aLoaded " + holograms.size() + " Holograms!"));
    }

    public void saveHolograms() {
        YamlConfiguration configuration=config.getConfiguration();
        for ( HologramMeta meta : holograms ) {
            String path="Holograms" + meta.getName();
            configuration.set(path + "location", LocationUtil.serialize(meta.getHologram().getLocation()));
            configuration.set(path + "type", meta.getType());
        }

        try {
            configuration.save(config.getFile());
        } catch (Exception e) {
            //
        }
    }
}
