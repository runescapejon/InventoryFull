package me.clip.inventoryfull.listeners;

import java.util.Iterator;
import java.util.List;
import me.clip.autosell.events.DropsToInventoryEvent;
import me.clip.inventoryfull.IFOptions;
import me.clip.inventoryfull.InventoryFull;
import me.clip.inventoryfull.events.InventoryFullEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;

public class AutoSellListener
  implements Listener
{
  private InventoryFull plugin;
  
  public AutoSellListener(InventoryFull i)
  {
    this.plugin = i;
  }
  
  @EventHandler(priority=EventPriority.MONITOR)
  public void onBlockToInv(DropsToInventoryEvent e)
  {
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
    if ((drops == null) || (drops.isEmpty())) {
      return;
    }
    PlayerInventory i = p.getInventory();
    if (!this.plugin.getTools().contains(i.getItemInHand().getType())) {
      return;
    }
    ItemStack wont = null;
    
    Iterator<ItemStack> iterator = drops.iterator();
    if (iterator.hasNext())
    {
      ItemStack drop = (ItemStack)iterator.next();
      if (this.plugin.getIgnored().contains(drop.getType())) {
        return;
      }
      ItemStack[] arrayOfItemStack;
      int j = (arrayOfItemStack = i.getContents()).length;
      for (int i1 = 0; i1 < j; i1++)
      {
        ItemStack is = arrayOfItemStack[i1];
        if (is == null) {
          return;
        }
        if ((is.getType().equals(drop.getType())) && (is.getAmount() + drop.getAmount() <= is.getMaxStackSize())) {
          return;
        }
      }
      wont = drop;
    }
    if (wont == null) {
      return;
    }
    String name = p.getName();
    if (this.plugin.isAlerted(name))
    {
      int current = this.plugin.getAlertAmount(name);
      if (current >= this.plugin.getOptions().getMaxAlerts()) {
        return;
      }
      this.plugin.setAlertAmount(name, current + 1);
    }
    else
    {
      this.plugin.setAlertAmount(name, 1);
    }
    this.plugin.decreaseAlertAmount(name);
    
    InventoryFullEvent event = new InventoryFullEvent(p, wont);
    
    Bukkit.getPluginManager().callEvent(event);
  }
}