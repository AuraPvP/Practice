package net.frozenorb.potpvp.kittype.command;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.reflect.TypeToken;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kt.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class KitTypeImportExportCommands {

	@Command(names={"kittype export"}, permission="op", async=true, description = "Export the kit types.")
	public static void executeExport(CommandSender sender) {
		String json = PotPvPND.plainGson.toJson(KitType.getAllTypes());
		try {
			Files.write(json, new File(PotPvPND.getInstance().getDataFolder(), "kitTypes.json"), Charsets.UTF_8);
			sender.sendMessage(ChatColor.GREEN + "Exported.");
		} catch (IOException e) {
			e.printStackTrace();
			sender.sendMessage(ChatColor.RED + "Failed to export.");
		}
	}

	@Command(names={"kittype import"}, permission="op", async=true, description = "Import the kit types.")
	public static void executeImport(CommandSender sender) {
		File file = new File(PotPvPND.getInstance().getDataFolder(), "kitTypes.json");
		if (file.exists()) {
			try (BufferedReader schematicsFileReader=Files.newReader(file, Charsets.UTF_8)) {
				Type schematicListType = new TypeToken<List<KitType>>() {
				}.getType();
				List<KitType> kitTypes = PotPvPND.getGson().fromJson(schematicsFileReader, schematicListType);
				for ( KitType kitType : kitTypes ) {
					KitType.getAllTypes().removeIf(otherKitType -> otherKitType.getId().equals(kitType.getId()));
					KitType.getAllTypes().add(kitType);
					kitType.saveAsync();
				}
			} catch (IOException e) {
				e.printStackTrace();
				sender.sendMessage(ChatColor.RED + "Failed to import.");
			}
		}
		sender.sendMessage(ChatColor.GREEN + "Imported.");
	}
}

