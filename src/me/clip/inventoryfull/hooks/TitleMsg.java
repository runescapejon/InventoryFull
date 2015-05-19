package me.clip.inventoryfull.hooks;

import io.puharesource.mc.titlemanager.api.ActionbarTitleObject;
import io.puharesource.mc.titlemanager.api.TitleObject;
import org.bukkit.entity.Player;

public class TitleMsg {
	
	public void sendTitle(Player p, String title, String subtitle, String wontFit, int in, int dur, int out) {
		TitleObject t = new TitleObject(title.replace("%player%", p.getName()).replace("%block%", wontFit), 
				subtitle.replace("%player%", p.getName()).replace("%block%", wontFit));
		t.setFadeIn(in);
		t.setStay(dur);
		t.setFadeOut(out);
		t.send(p);
	}

	public void sendActionbar(Player p, String msg, String wontFit) {
		ActionbarTitleObject ta = new ActionbarTitleObject(msg.replace("%player%", p.getName()).replace("%block%", wontFit));
		ta.send(p);
	}
}
