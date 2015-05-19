package me.clip.inventoryfull.hooks;

import java.util.List;
import java.util.Random;
import me.clip.actionannouncer.ActionAPI;
import me.clip.inventoryfull.InventoryFull;
import org.bukkit.entity.Player;

public class ActionMsg {
	
	Random r;
	
	InventoryFull plugin;

	public ActionMsg(InventoryFull i) {
		this.plugin = i;
		this.r = new Random();
	}

	public void send(Player p, List<String> msg, String block, int time) {
		
		if (msg == null || msg.isEmpty()) {
			return;
		}
		
		String send = "&cYour inventory is full!";
		
		if (msg.size() > 1) {
			send = msg.get(r.nextInt(msg.size()));
		} else {
			send = msg.get(0);
		}
		
		send = send.replace("%player%", p.getName()).replace("%block%", block);

		ActionAPI.sendTimedPlayerAnnouncement(plugin, p, send, time);
	}
}
