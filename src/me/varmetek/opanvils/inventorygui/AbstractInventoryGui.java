package me.varmetek.opanvils.inventorygui;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractInventoryGui<T extends Inventory> implements Listener
{

  protected Map<UUID,GuiView<T>> invs = new HashMap<>();
  protected ActionItemManager actionItems = new ActionItemManager();

  protected final Plugin plugin;


  public ActionItemManager getActionItemManager(){
    return actionItems;
  }

  public AbstractInventoryGui (Plugin plugin){
    Validate.notNull(plugin);
    this.plugin = plugin;
    Bukkit.getPluginManager().registerEvents(this,plugin);

  }




  public GuiView<T>  open(Player player){
    Validate.notNull(player);
    GuiView<T> view = createView(player);
    invs.put(player.getUniqueId(),view);
    return view;


  }
  public GuiView<T>  getView(Player player){
    return invs.get(player.getUniqueId());
  }
  public GuiView<T>  getView(UUID player){
    return invs.get(player);
  }


  protected abstract GuiView<T> createView(Player p);

  public Plugin getPlugin(){
    return plugin;
  }
  /**
   * Tests whether the identity of {@code i} is the same as the inventory of this window.
   * **/

  public boolean isThisInv(T i){
    return i != null
             && i.getHolder() != null
             && i.getHolder() instanceof GuiView
             && ((GuiView)i.getHolder()).handle == this;
  }



  public boolean isOpen(){
    return !invs.isEmpty();
  }

  public void closeAll(){
    for(UUID id: invs.keySet()){
      Player pl = Bukkit.getPlayer(id);
      pl.closeInventory();
    }

   // for(GuiView view : invs.values()){
    //  view.dispose();
    //}

    invs.clear();
  }


  public void dispose(){
    closeAll();

    HandlerList.unregisterAll(this);;
    invs = null;
    actionItems.clear();
    actionItems = null;
  }

  @EventHandler
  public void _clickEvent(InventoryClickEvent ev){
    // System.out.println("*YAYAYAYY");

    if(!isThisInv((T)ev.getView().getTopInventory())) return;

    Player player = (Player)ev.getWhoClicked();
    ClickType click = ev.getClick();
    GuiView<T> view = getView(player);
    if(view == null) return;


    if(ev.getClick() == ClickType.WINDOW_BORDER_LEFT || ev.getClick() == ClickType.WINDOW_BORDER_RIGHT){
      ev.setCancelled(view.onClickOutside());
      return;
    }else {
      ActionItem item = view.getActionItems().getItem(ev.getCurrentItem());
      if (item != null){
        ev.setCancelled(item.onClick(player, click));
      } else {
        ev.setCancelled(view.onClickDefault(ev));
      }
    }
    // clickEvent.accept(ev);
  }

 // @EventHandler()
  public void _openEvent(InventoryOpenEvent ev){
    if(!isThisInv((T)ev.getView().getTopInventory())) return;
    UUID id = ev.getPlayer().getUniqueId();
    GuiView<T> view = getView(id);
    if(view == null) return;

    ev.setCancelled(view.onInvOpen());

    //setupInv.accept((T)ev.getInventory());
    // openEvent.accept(ev);
  }


  @EventHandler(priority = EventPriority.MONITOR)
  public void _afterOpenEvent(InventoryOpenEvent ev){
    if(!isThisInv((T)ev.getView().getTopInventory())) return;
    UUID id = ev.getPlayer().getUniqueId();
    GuiView<T> view = getView(id);
    if(view == null) return;

    if(view.onInvOpen()){
      ev.setCancelled(true);
      invs.get(id).dispose();
      invs.remove(id);
    }

  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void _closeEvent(InventoryCloseEvent ev){
    if(!isThisInv((T)ev.getInventory())) return;
    UUID id = ev.getPlayer().getUniqueId();
    GuiView<T> view = getView(id);
    if(view == null) return;
    if(view.onInvClose()){
      ev.getPlayer().openInventory(ev.getInventory());

    }else{
      view.dispose();
      invs.remove(id);
      ImmutableList<HumanEntity> views = ImmutableList.copyOf(ev.getInventory().getViewers());
      for (HumanEntity ent : views) {
        if(ent == null)continue;
        ent.closeInventory();
      }

    }

    //setupInventory((T)ev.getInventory());

    //invs.remove(ev.getPlayer().getUniqueId());
    // closeEvent.accept(ev);


  }








}

