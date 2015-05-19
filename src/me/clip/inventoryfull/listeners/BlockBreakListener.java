package me.clip.inventoryfull.listeners;

import java.util.Iterator;

import me.clip.inventoryfull.InventoryFull;
import me.clip.inventoryfull.events.InventoryFullEvent;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class BlockBreakListener implements Listener {
	
	private InventoryFull plugin;

	public BlockBreakListener(InventoryFull i) {
		this.plugin = i;
	}
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent e) {

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

		Block b = e.getBlock();

		PlayerInventory i = p.getInventory();

		if (!plugin.getTools().contains(i.getItemInHand().getType())) {
			return;
		}

		if (plugin.getIgnored().contains(b.getType())) {
			return;
		}

		if (b.getDrops(i.getItemInHand()) == null || b.getDrops(i.getItemInHand()).isEmpty()) {
			return;
		}

		ItemStack wont = null;

		Iterator<ItemStack> iterator = b.getDrops(i.getItemInHand()).iterator();

		while (iterator.hasNext()) {

			ItemStack drop = iterator.next();

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
