package me.clip.inventoryfull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.clip.inventoryfull.events.InventoryFullEvent;
import me.clip.inventoryfull.hooks.ActionMsg;
import me.clip.inventoryfull.hooks.HoloMsg;
import me.clip.inventoryfull.hooks.TitleMsg;
import me.clip.inventoryfull.listeners.AutoSellListener;
import me.clip.inventoryfull.listeners.BlockBreakListener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class InventoryFull extends JavaPlugin implements Listener {
		
	private final Map<String, Integer> active = new HashMap<String, Integer>();
	
	private IFOptions options;
	
	private HoloMsg holo;
	private ActionMsg aa;
	private TitleMsg tm;

	@Override
	public void onEnable() {
		
		loadDefConfig();
		
		options = new IFOptions(this);

		if (Bukkit.getPluginManager().isPluginEnabled("AutoSell")) {
			
			Bukkit.getServer().getPluginManager().registerEvents(new AutoSellListener(this), this);
			getLogger().info("*** Hooked into AutoSell! ***");
		
		} else {
			
			Bukkit.getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
		}
		
		initHooks();
		
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		
		getCommand("inventoryfull").setExecutor(this);
	}

	private void initHooks() {
		
		IFOptions options = getOptions();

		if (options.useActionAnnouncer()) {
			if (Bukkit.getPluginManager().isPluginEnabled("ActionAnnouncer")) {

				if (aa == null) {
					aa = new ActionMsg(this);
				}
				getLogger().info("*** Hooked into ActionAnnouncer! ***");

			} else {

				getLogger().info("*** Could not hook into ActionAnnouncer! ***");
			}
		}
		
		if (options.useTitleManager() || options.useTitleABar()) {
			if (Bukkit.getPluginManager().isPluginEnabled("TitleManager")) {

				if (tm == null) {
					tm = new TitleMsg();
				}
				getLogger().info("*** Hooked into TitleManager! ***");

			} else {

				getLogger().info("*** Could not hook into TitleManager! ***");
			}
		}
		
		if (options.useHolo()) {
			if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {

				if (holo == null) {
					holo = new HoloMsg(this);
				}
				getLogger().info("*** Hooked into HolographicDisplays! ***");

			} else {

				getLogger().info("*** Could not hook into HolographicDisplays! ***");
			}
		}
	}

	private void loadDefConfig() {
		FileConfiguration c = getConfig();
		c.options().header("InventoryFull version "
						+ getDescription().getVersion()
						+ "\nCreated by: extended_clip"
						+ "\nValid placeholders:"
						+ "\n%block% - display the dropped item type"
						+ "\n%player% - display the players name"
						+ "\n  "
						+ "\nFor valid sounds, visit http://jd.bukkit.org/rb/apidocs/org/bukkit/Sound.html");
		c.addDefault("cooldown_time", Integer.valueOf(5));
		c.addDefault("max_alerts_until_cooldown", Integer.valueOf(5));
		c.addDefault("sound_when_full.enabled", Boolean.valueOf(true));
		c.addDefault("sound_when_full.sound", "NOTE_PLING");
		c.addDefault("sound_when_full.volume", Integer.valueOf(10));
		c.addDefault("sound_when_full.pitch", Integer.valueOf(1));
		c.addDefault("chat_message.use_chat_message", Boolean.valueOf(true));
		c.addDefault("chat_message.message", Arrays.asList(new String[] { "&cYour inventory is full!" }));
		c.addDefault("actionannouncer.use_actionbar", Boolean.valueOf(false));
		c.addDefault("actionannouncer.display_time", Integer.valueOf(5));
		c.addDefault("actionannouncer.message", Arrays.asList(new String[] { "&cYour inventory is full!",
						"&4Your inventory is full!" }));
		c.addDefault("titlemanager.use_title", Boolean.valueOf(false));
		c.addDefault("titlemanager.title", "&cYou don't have room in your inventory");
		c.addDefault("titlemanager.subtitle", "to collect that &f%block%&c!");
		c.addDefault("titlemanager.fade_in", Integer.valueOf(12));
		c.addDefault("titlemanager.fade_out", Integer.valueOf(12));
		c.addDefault("titlemanager.duration", Integer.valueOf(20));
		c.addDefault("titlemanager.use_actionbar", Boolean.valueOf(false));
		c.addDefault("titlemanager.actionbar_message", "&cYou don't have room in your inventory");
		c.addDefault("holographicdisplays.use_hologram", Boolean.valueOf(false));
		c.addDefault("holographicdisplays.message", Arrays.asList(new String[] {
				"&cYour inventory", "&cis full!" }));
		c.addDefault("holographicdisplays.display_time", Integer.valueOf(3));
		c.options().copyDefaults(true);
		saveConfig();
	}

	@EventHandler
	public void onFull(InventoryFullEvent e) {
		
		Player p = e.getPlayer();
		
		if (p == null || e.getItem() == null) {
			return;
		}
		
		IFOptions opt = getOptions();
		
		String type = e.getItem().getType().name();
		
		if (opt.useChatMsg()) {
			for (String line : opt.getChatMsg()) {
				sms(p, line.replace("%player%", p.getName()).replace("%block%", type));
			}
		}

		if (opt.useHolo() && holo != null) {
			holo.send(p, opt.getHoloMsg(), type);
		}

		if (opt.useActionAnnouncer() && aa != null) {
			aa.send(p, opt.getActionMsg(), type, opt.getActionTime());
		}

		if (opt.useTitleManager() && this.tm != null) {
			this.tm.sendTitle(p, opt.getTitleMsg(),
					opt.getSubTitleMsg(), type, opt.getFadeIn(),
					opt.getDuration(), opt.getFadeOut());
		}

		if (opt.useTitleABar() && tm != null) {
			this.tm.sendActionbar(p, opt.getTitleABarMsg(), type);
		}
		
		if (!opt.useSound()) {
			return;
		}
		
		try {
			
			Sound s = Sound.valueOf(getOptions().getSound().toUpperCase());
			
			if (s != null) {
				p.playSound(p.getLocation(), s, getOptions().getVolume(), opt.getPitch());
			}
			
		} catch (Exception ex) {
			getLogger().warning("Your inventory full sound "+opt.getSound()+" is invalid!");
			getLogger().info("Valid sound names can be found at the following link:");
			getLogger().info("https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html");
			
		}
	}

	@Override
	public boolean onCommand(CommandSender s, Command command, String label, String[] args) {
		
		if ((s instanceof Player)) {
			
			Player p = (Player) s;
			
			if (!p.hasPermission("inventoryfull.admin")) {
				sms(s, "&cYou don't have permission to do that!");
				return true;
			}
		}
		
		if (args.length == 0) {
			
			sms(s, "&cInventoryFull &fversion " + getDescription().getVersion());
			sms(s, "&7Created by: &cextended_clip");
			sms(s, "&7/invfull reload &f- &cReload config file");
			
		} else if ((args.length > 0) && (args[0].equalsIgnoreCase("reload"))) {
			
			reloadConfig();
			saveConfig();
			options = new IFOptions(this);
			holo = null;
			aa = null;
			tm = null;
			initHooks();
			sms(s, "&cInventoryFull &7configuration successfully reloaded!");
			
		} else {
			
			sms(s, "&cIncorrect usage! Use &7/inventoryfull");
		}
		return true;
	}
	
	public IFOptions getOptions() {
		
		if (options == null) {
			options = new IFOptions(this);
		}
		
		return options;
	}
	
	public boolean isAlerted(final Player p) {
		return active.containsKey(p.getName());
	}
	
	public int getAlertAmount(final Player p) {
		if (isAlerted(p)) {
			return active.get(p);
		}
		return 0;
	}
	
	public void setAlertAmount(Player p, int i) {
		active.put(p.getName(), i);
	}
	
	public void decreaseAlertAmount(final String name) {
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			
			@Override
			public void run() {
				
				if (active.containsKey(name)) {
					
					if (active.get(name) == 1) {
						
						active.remove(name);
					} else {
						
						active.put(name, active.get(name) - 1);
					}
				}
			}
		}, 20L * options.getCooldownTime());
	}

	public void sms(CommandSender s, String msg) {
		s.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}

	private final List<Material> tools = Arrays.asList(new Material[] {
			Material.STONE_AXE, Material.STONE_HOE, Material.STONE_PICKAXE,
			Material.STONE_SPADE, Material.WOOD_AXE, Material.WOOD_HOE,
			Material.WOOD_PICKAXE, Material.WOOD_SPADE, Material.IRON_AXE,
			Material.IRON_HOE, Material.IRON_PICKAXE, Material.IRON_SPADE,
			Material.GOLD_AXE, Material.GOLD_HOE, Material.GOLD_PICKAXE,
			Material.GOLD_SPADE, Material.DIAMOND_AXE, Material.DIAMOND_HOE,
			Material.DIAMOND_PICKAXE, Material.DIAMOND_SPADE });

	private final List<Material> ignored = Arrays.asList(new Material[] {
			Material.LONG_GRASS, Material.GRASS, Material.WHEAT,
			Material.SUGAR_CANE_BLOCK, Material.SUGAR_CANE, Material.BED_BLOCK,
			Material.BED, Material.BOAT, Material.BOOKSHELF,
			Material.BREWING_STAND, Material.BREWING_STAND_ITEM,
			Material.CACTUS, Material.CAKE, Material.CAKE_BLOCK,
			Material.CARPET, Material.CARROT, Material.CARROT_ITEM,
			Material.CAULDRON, Material.CAULDRON_ITEM, Material.CROPS,
			Material.COMMAND, Material.COMMAND_MINECART,
			Material.DAYLIGHT_DETECTOR, Material.DEAD_BUSH,
			Material.DETECTOR_RAIL, Material.DIODE, Material.DIODE_BLOCK_OFF,
			Material.DIODE_BLOCK_ON, Material.DOUBLE_PLANT, Material.DISPENSER,
			Material.DROPPER, Material.ENCHANTMENT_TABLE, Material.ENDER_CHEST,
			Material.ENDER_PORTAL_FRAME, Material.EXPLOSIVE_MINECART,
			Material.FLOWER_POT, Material.FLOWER_POT_ITEM, Material.HOPPER,
			Material.HOPPER_MINECART, Material.ICE, Material.IRON_DOOR_BLOCK,
			Material.IRON_DOOR, Material.ITEM_FRAME, Material.JUKEBOX,
			Material.LEAVES, Material.LEAVES_2, Material.LEVER,
			Material.MELON_BLOCK, Material.MELON_STEM, Material.MINECART,
			Material.NOTE_BLOCK, Material.PACKED_ICE, Material.PAINTING,
			Material.PISTON_BASE, Material.PISTON_EXTENSION,
			Material.PISTON_STICKY_BASE, Material.PISTON_MOVING_PIECE,
			Material.POISONOUS_POTATO, Material.POTATO, Material.PORTAL,
			Material.POWERED_MINECART, Material.POWERED_RAIL, Material.RAILS,
			Material.RED_ROSE, Material.YELLOW_FLOWER, Material.REDSTONE,
			Material.REDSTONE_COMPARATOR, Material.REDSTONE_WIRE,
			Material.SAPLING, Material.SEEDS, Material.SIGN,
			Material.SIGN_POST, Material.SNOW, Material.SNOW_BLOCK,
			Material.STAINED_GLASS, Material.STAINED_GLASS_PANE,
			Material.STORAGE_MINECART, Material.TNT, Material.TRAP_DOOR,
			Material.TORCH, Material.TRAPPED_CHEST, Material.TRIPWIRE,
			Material.TRIPWIRE_HOOK, Material.VINE, Material.WALL_SIGN,
			Material.WATER_LILY, Material.WEB });
	
	public List<Material> getIgnored() {
		return ignored;
	}
	
	public List<Material> getTools() {
		return tools;
	}
}