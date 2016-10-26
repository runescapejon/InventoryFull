package me.clip.inventoryfull.listeners;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import me.clip.inventoryfull.IFOptions;
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
import org.bukkit.plugin.PluginManager;

public class BlockBreakListener
  implements Listener
{
  private InventoryFull plugin;
  
  public BlockBreakListener(InventoryFull i)
  {
    this.plugin = i;
  }
  
  @EventHandler(priority=EventPriority.NORMAL)
  public void onBlockBreak(BlockBreakEvent e)
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
    Block b = e.getBlock();
    
    PlayerInventory inv = p.getInventory();
    if (inv.getItemInHand() != null)
    {
      if (this.plugin.getTools().contains(inv.getItemInHand().getType())) {}
    }
    else {
      return;
    }
    if (this.plugin.getIgnored().contains(b.getType())) {
      return;
    }
    Collection<ItemStack> d = b.getDrops(inv.getItemInHand());
    if ((d == null) || (d.isEmpty())) {
      return;
    }
    ItemStack wont = null;
    
    Iterator<ItemStack> i = d.iterator();
    if (i.hasNext())
    {
      ItemStack drop = (ItemStack)i.next();
      ItemStack[] arrayOfItemStack;
      int j = (arrayOfItemStack = inv.getContents()).length;
      for (int i1 = 0; i1 < j; i1++)
      {
        ItemStack is = arrayOfItemStack[i1];
        if (is == null) {
          return;
        }
        if ((is.getType() == drop.getType()) && (is.getAmount() + drop.getAmount() <= is.getMaxStackSize())) {
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
