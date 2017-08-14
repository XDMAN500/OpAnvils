package me.varmetek.opanvils.inventorygui;


import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * Created by XDMAN500 on 7/20/2017.
 */
public abstract class ActionItem
{
  protected final ItemStack item;
  protected final int hash;

  public ActionItem(ItemStack item){
    Preconditions.checkNotNull(item,"Item cannot be null");
    this.item = item.clone();
    hash = this.item.hashCode();
  }

  public abstract boolean onClick(Player pl, ClickType click);


  public ItemStack toItemStack(){
    return item.clone();
  }

  @Override
  public int hashCode(){

    return hash;
  }

  @Override
  public boolean equals(Object c){
    if(c == null) return false;
    if(c.getClass() !=   this.getClass())return false;
    return c.hashCode() == this.hashCode();
  }
}
