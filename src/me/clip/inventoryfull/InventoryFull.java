package me.clip.inventoryfull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import me.clip.inventoryfull.events.InventoryFullEvent;
import me.clip.inventoryfull.hooks.HoloMsg;
import me.clip.inventoryfull.listeners.AutoSellListener;
import me.clip.inventoryfull.listeners.BlockBreakListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class InventoryFull
  extends JavaPlugin
  implements Listener
{
  private static Map<String, Integer> active = new HashMap();
  private IFOptions options;
  private HoloMsg holo;
  
  public void onEnable()
  {
    loadDefConfig();
    
    this.options = new IFOptions(this);
    if (Bukkit.getPluginManager().isPluginEnabled("AutoSell"))
    {
      Bukkit.getServer().getPluginManager().registerEvents(new AutoSellListener(this), this);
      getLogger().info("*** Hooked into AutoSell! ***");
    }
    else
    {
      Bukkit.getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
    }
    initHooks();
    
    Bukkit.getServer().getPluginManager().registerEvents(this, this);
    
    getCommand("inventoryfull").setExecutor(this);
  }
  
  private void initHooks()
  {
    IFOptions opt = getOptions(); 
    if (opt.useHolo()) {
      if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays"))
      {
        if (this.holo == null) {
          this.holo = new HoloMsg(this);
        }
        getLogger().info("*** Hooked into HolographicDisplays! ***");
      }
      else
      {
        getLogger().info("*** Could not hook into HolographicDisplays! ***");
      }
    }
  }
  
  private void loadDefConfig()
  {
    FileConfiguration c = getConfig();
    c.options().header("InventoryFull version " + 
      getDescription().getVersion() + 
      "\nCreated by: extended_clip" + 
      "\nValid placeholders:" + 
      "\n%block% - display the dropped item type" + 
      "\n%player% - display the players name" + 
      "\n  " + 
      "\nFor valid sounds, visit http://jd.bukkit.org/rb/apidocs/org/bukkit/Sound.html");
    c.addDefault("cooldown_time", Integer.valueOf(5));
    c.addDefault("max_alerts_until_cooldown", Integer.valueOf(5));
    c.addDefault("sound_when_full.enabled", Boolean.valueOf(true));
    c.addDefault("sound_when_full.sound", "NOTE_PLING");
    c.addDefault("sound_when_full.volume", Integer.valueOf(10));
    c.addDefault("sound_when_full.pitch", Integer.valueOf(1));
    c.addDefault("chat_message.use_chat_message", Boolean.valueOf(true));
    c.addDefault("chat_message.message", Arrays.asList(new String[] { "&cYour inventory is full!" }));
    c.addDefault("holographicdisplays.use_hologram", Boolean.valueOf(false));
    c.addDefault("holographicdisplays.message", Arrays.asList(new String[] {
      "&cYour inventory", "&cis full!" }));
    c.addDefault("holographicdisplays.display_time", Integer.valueOf(3));
    c.options().copyDefaults(true);
    saveConfig();
  }
  
  @EventHandler
  public void onFull(InventoryFullEvent e)
  {
    Player p = e.getPlayer();
    if ((p == null) || (e.getItem() == null)) {
      return;
    }
    IFOptions opt = getOptions();
    
    String type = e.getItem().getType().name();
    if (opt.useChatMsg()) {
      for (String line : opt.getChatMsg()) {
        sms(p, line.replace("%player%", p.getName()).replace("%block%", type));
      }
    }
    if ((opt.useHolo()) && (this.holo != null)) {
      this.holo.send(p, opt.getHoloMsg(), type);
    }
    if (!opt.useSound()) {
      return;
    }
    try
    {
      Sound s = Sound.valueOf(getOptions().getSound().toUpperCase());
      if (s != null) {
        p.playSound(p.getLocation(), s, getOptions().getVolume(), opt.getPitch());
      }
    }
    catch (Exception ex)
    {
      getLogger().warning("Your inventory full sound " + opt.getSound() + " is invalid!");
      getLogger().info("Valid sound names can be found at the following link:");
      getLogger().info("https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html");
    }
  }
  
  public boolean onCommand(CommandSender s, Command command, String label, String[] args)
  {
    if ((s instanceof Player))
    {
      Player p = (Player)s;
      if (!p.hasPermission("inventoryfull.admin"))
      {
        sms(s, "&cYou don't have permission to do that!");
        return true;
      }
    }
    if (args.length == 0)
    {
      sms(s, "&cInventoryFull &fversion " + getDescription().getVersion());
      sms(s, "&7Created by: &cextended_clip");
      sms(s, "&7/invfull reload &f- &cReload config file");
    }
    else if ((args.length > 0) && (args[0].equalsIgnoreCase("reload")))
    {
      reloadConfig();
      saveConfig();
      this.options = new IFOptions(this);
      this.holo = null;
      initHooks();
      sms(s, "&cInventoryFull &7configuration successfully reloaded!");
    }
    else
    {
      sms(s, "&cIncorrect usage! Use &7/inventoryfull");
    }
    return true;
  }
  
  public IFOptions getOptions()
  {
    if (this.options == null) {
      this.options = new IFOptions(this);
    }
    return this.options;
  }
  
  public boolean isAlerted(String name)
  {
    if ((active == null) || (active.isEmpty())) {
      return false;
    }
    return active.containsKey(name);
  }
  
  public int getAlertAmount(String name)
  {
    if (isAlerted(name)) {
      return ((Integer)active.get(name)).intValue();
    }
    return 0;
  }
  
  public void setAlertAmount(String name, int i)
  {
    if (active == null) {
      active = new HashMap();
    }
    active.put(name, Integer.valueOf(i));
  }
  
  public void decreaseAlertAmount(final String name)
  {
    Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable()
    {
      public void run()
      {
        if (InventoryFull.active == null) {
          return;
        }
        if (InventoryFull.active.containsKey(name))
        {
          int c = ((Integer)InventoryFull.active.get(name)).intValue();
          if (c == 1) {
            InventoryFull.active.remove(name);
          } else {
            InventoryFull.active.put(name, Integer.valueOf(c - 1));
          }
        }
      }
    }, 20L * this.options.getCooldownTime());
  }
  
  public void sms(CommandSender s, String msg)
  {
    s.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
  }
  
  private final List<Material> tools = Arrays.asList(new Material[] {Material.STONE_AXE, Material.STONE_HOE, Material.STONE_PICKAXE, Material.STONE_SPADE, Material.WOOD_AXE, Material.WOOD_HOE, Material.WOOD_PICKAXE, Material.WOOD_SPADE, Material.IRON_AXE, Material.IRON_HOE, Material.IRON_PICKAXE, Material.IRON_SPADE, Material.GOLD_AXE, Material.GOLD_HOE, Material.GOLD_PICKAXE, Material.GOLD_SPADE, Material.DIAMOND_AXE, Material.DIAMOND_HOE, Material.DIAMOND_PICKAXE, Material.DIAMOND_SPADE });
  private final List<Material> ignored = Arrays.asList(new Material[] {Material.LONG_GRASS, Material.GRASS, Material.WHEAT, Material.SUGAR_CANE_BLOCK, Material.SUGAR_CANE, Material.BED_BLOCK, Material.BED, Material.BOAT, Material.BOOKSHELF, Material.BREWING_STAND, Material.BREWING_STAND_ITEM, Material.CACTUS, Material.CAKE, Material.CAKE_BLOCK, Material.CARPET, Material.CARROT, Material.CARROT_ITEM, Material.CAULDRON, Material.CAULDRON_ITEM, Material.CROPS, Material.COMMAND, Material.COMMAND_MINECART, Material.DAYLIGHT_DETECTOR, Material.DEAD_BUSH, Material.DETECTOR_RAIL, Material.DIODE, Material.DIODE_BLOCK_OFF, Material.DIODE_BLOCK_ON, Material.DOUBLE_PLANT, Material.DISPENSER, Material.DROPPER, Material.ENCHANTMENT_TABLE, Material.ENDER_CHEST, Material.ENDER_PORTAL_FRAME, Material.EXPLOSIVE_MINECART, Material.FLOWER_POT, Material.FLOWER_POT_ITEM, Material.HOPPER, Material.HOPPER_MINECART, Material.ICE, Material.IRON_DOOR_BLOCK, Material.IRON_DOOR, Material.ITEM_FRAME, Material.JUKEBOX, Material.LEAVES, Material.LEAVES_2, Material.LEVER, Material.MELON_BLOCK, Material.MELON_STEM, Material.MINECART, Material.NOTE_BLOCK, Material.PACKED_ICE, Material.PAINTING, Material.PISTON_BASE, Material.PISTON_EXTENSION, Material.PISTON_STICKY_BASE, Material.PISTON_MOVING_PIECE, Material.POISONOUS_POTATO, Material.POTATO, Material.PORTAL, Material.POWERED_MINECART, Material.POWERED_RAIL, Material.RAILS, Material.RED_ROSE, Material.YELLOW_FLOWER, Material.REDSTONE, Material.REDSTONE_COMPARATOR, Material.REDSTONE_WIRE, Material.SAPLING, Material.SEEDS, Material.SIGN, Material.SIGN_POST, Material.SNOW, Material.SNOW_BLOCK, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE, Material.STORAGE_MINECART, Material.TNT, Material.TRAP_DOOR, Material.TORCH, Material.TRAPPED_CHEST, Material.TRIPWIRE, Material.TRIPWIRE_HOOK, Material.VINE, Material.WALL_SIGN, Material.WATER_LILY, Material.WEB });
  
  public List<Material> getIgnored()
  {
    return this.ignored;
  }
  
  public List<Material> getTools()
  {
    return this.tools;
  }
}
