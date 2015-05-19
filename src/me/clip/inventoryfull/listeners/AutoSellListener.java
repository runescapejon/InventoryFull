package me.clip.inventoryfull.listeners;

import java.util.Iterator;
import java.util.List;

import me.clip.autosell.events.DropsToInventoryEvent;
import me.clip.inventoryfull.InventoryFull;
import me.clip.inventoryfull.events.InventoryFullEvent;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class AutoSellListener implements Listener {
	
	private InventoryFull plugin;

	public AutoSellListener(InventoryFull i) {
		this.plugin = i;
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void onBlockToInv(DropsToInventoryEvent e) {

		if (e.isCancelled()) {
			return;
		}

		Player p = e.getPlayer();

		if (!p.getGameMode().equals(GameMode.SURVIVAL)) {
			return;
		}

		if (!p.hasPermission("inventoryfull.alert")) {
			return;
		}

		List<ItemStack> drops = e.getDrops();

		if (drops == null || drops.isEmpty()) {
			return;
		}

		PlayerInventory i = p.getInventory();

		if (!plugin.getTools().contains(i.getItemInHand().getType())) {
			return;
		}

		ItemStack wont = null;

		Iterator<ItemStack> iterator = drops.iterator();

		if (iterator.hasNext()) {

			ItemStack drop = iterator.next();
			
			if (plugin.getIgnored().contains(drop.getType())) {
				return;
			}

			for (ItemStack is : i.getContents()) {

				if (is == null) {
					return;
				}

				if (is.getType().equals(drop.getType()) && is.getAmount() + drop.getAmount() <= is.getMaxStackSize()) {
					return;
				}
			}

			wont = drop;
		}

		if (wont == null) {
			return;
		}

		if (plugin.isAlerted(p)) {
			
			int current = plugin.getAlertAmount(p);

			if (current >= plugin.getOptions().getMaxAlerts()) {
				return;
			}

			plugin.setAlertAmount(p, current+1);

		} else {
			plugin.setAlertAmount(p, 1);
		}

		plugin.decreaseAlertAmount(p.getName());

		InventoryFullEvent event = new InventoryFullEvent(p, wont);

		Bukkit.getPluginManager().callEvent(event);
	}
}
