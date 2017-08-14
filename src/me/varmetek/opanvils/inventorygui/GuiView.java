package me.varmetek.opanvils.inventorygui;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class GuiView<T extends Inventory> implements InventoryHolder
{
  protected Player player;
  protected T inventory;
  protected AbstractInventoryGui handle;
  protected ActionItemManager actionItems;

  public GuiView(AbstractInventoryGui<T> handle ,Player player){

    this.player = Preconditions.checkNotNull(player,"Player cannot be null");
    this.handle = Preconditions.checkNotNull(handle,"Handler cannot be null");
    this.inventory =Preconditions.checkNotNull(createInventory(),"Inventory cannot be null");
    this.actionItems = new ActionItemManager(handle.actionItems);




    this.player.openInventory(inventory);
  }

  public Player getPlayer (){
    return player;
  }

  @Override
  public T getInventory(){
    return this.inventory;
  }


  public ActionItemManager getActionItems (){
    return actionItems;
  }

  public AbstractInventoryGui<T> getHandle(){
    return handle;
  }

  protected void dispose(){

    actionItems.clear();
    inventory.clear();
    inventory = null;
    actionItems = null;
    handle = null;
    player = null;
  }

  public abstract T createInventory();
  public abstract void refresh();
  public abstract boolean onClickDefault(InventoryClickEvent ev);
  public abstract boolean onClickOutside();
  public abstract boolean onInvOpen();
  public abstract boolean onInvClose();
}
