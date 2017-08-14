package me.varmetek.opanvils.inventorygui;

import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * Manages the registration of all the {@link ActionItem}s
 * */

public class ActionItemManager
{

  //The keys are integers for easy universal lookups using hashcodes
  // Action item can be grabed from the itemstack or the
  protected final Map<Integer,ActionItem> items;

  public ActionItemManager(){
    items = new HashMap<>();
  }

  public ActionItemManager(ActionItemManager man){
    items = new HashMap<>(man.items);
  }

  public void addItem(ActionItem item){
    items.put(item.hashCode(),item);
  }

  public void removeItem(ItemStack i){
    items.remove(i.hashCode());
  }

  public void removeItem(ActionItem i){
    items.remove(i.hashCode());
  }

  public void clear(){
    items.clear();
  }

  public Collection<ActionItem> getItems(){
    return items.values();
  }

  public boolean containsItem(ItemStack i){
    return items.containsKey(i.hashCode());
  }

  public boolean contiansItem(ActionItem i){
    return items.containsKey(i.hashCode());
  }

  public ActionItem getItem(ItemStack i){

    return items.get(i.hashCode());
  }

}
