package me.clip.inventoryfull.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class InventoryFullEvent
  extends Event
{
  private static final HandlerList handlers = new HandlerList();
  private String pname;
  private ItemStack wontFit;
  
  public InventoryFullEvent(Player p, ItemStack wontFit)
  {
    this.pname = p.getName();
    this.wontFit = wontFit;
  }
  
  public String getName()
  {
    return this.pname;
  }
  
  public Player getPlayer()
    throws NullPointerException
  {
    return Bukkit.getServer().getPlayer(this.pname);
  }
  
  public HandlerList getHandlers()
  {
    return handlers;
  }
  
  public static HandlerList getHandlerList()
  {
    return handlers;
  }
  
  public ItemStack getItem()
  {
    return this.wontFit;
  }
}