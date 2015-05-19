package me.clip.inventoryfull.hooks;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import java.util.List;
import me.clip.inventoryfull.InventoryFull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class HoloMsg {
	private InventoryFull plugin;

	public HoloMsg(InventoryFull i) {
		this.plugin = i;
	}

	public void send(Player p, List<String> msg, String wontFit) {
		
		Vector v = p.getLocation().getDirection().multiply(1);
		
		Location dLoc = p.getEyeLocation().add(v);

		Hologram full = HologramsAPI.createHologram(this.plugin, dLoc);
		
		for (String line : msg) {
			full.appendTextLine(ChatColor.translateAlternateColorCodes('&',
					line.replace("%player%", p.getName()).replace("%block%", wontFit)));
		}
		
		removeHologram(full);
	}

	private void removeHologram(final Hologram h) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
				new Runnable() {

					@Override
					public void run() {
						h.delete();
					}
				}, 20L * plugin.getOptions().getHoloTime());
	}
}
