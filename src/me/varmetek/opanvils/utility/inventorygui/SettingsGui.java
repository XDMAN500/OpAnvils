package me.varmetek.opanvils.utility.inventorygui;

import me.varmetek.opanvils.inventorygui.AbstractInventoryGui;
import me.varmetek.opanvils.inventorygui.ActionItem;
import me.varmetek.opanvils.inventorygui.GuiView;
import me.varmetek.opanvils.OpAnvilsPlugin;
import me.varmetek.opanvils.utility.ConfigManager;
import me.varmetek.opanvils.utility.PermissionUtil;
import me.varmetek.opanvils.utility.PlayerHandler;
import me.varmetek.opanvils.utility.TextUtil;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SettingsGui extends AbstractInventoryGui<Inventory>
{

  protected  final ReloadConfigItem reloadConfigItem  = getReloadConfigItem();
  protected  final ReloadPlayerItem reloadPlayerItem = getReloadPlayerItem();

  public SettingsGui (OpAnvilsPlugin plugin){
    super(plugin);

  }


  @Override
  protected GuiView<Inventory> createView (Player p){
    return new SettingsView(this,p);
  }


  private  ReloadPlayerItem getReloadPlayerItem(){
    ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE,1 , DyeColor.BLUE.getWoolData());
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(TextUtil.color("&9Reload Players"));
    item.setItemMeta(meta);

    ReloadPlayerItem aitem = new  ReloadPlayerItem (item);
    this.actionItems.addItem(aitem);;
    return aitem;
  }

  private  ReloadConfigItem getReloadConfigItem(){
    ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE,1 , DyeColor.GREEN.getWoolData());
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(TextUtil.color("&aReload Config"));
    item.setItemMeta(meta);

    ReloadConfigItem aitem = new  ReloadConfigItem (item);
    this.actionItems.addItem(aitem);;
    return aitem;
  }



  public class ReloadPlayerItem extends ActionItem
  {

    protected ReloadPlayerItem (ItemStack item){
      super(item);
    }

    @Override
    public boolean onClick (Player pl, ClickType click){
      if(pl.hasPermission(PermissionUtil.getAdminPermission())){
        PlayerHandler handle = ((OpAnvilsPlugin) SettingsGui.this.plugin).getPlayerHandler();
        handle.loadAllPlayers();
        TextUtil.sendMessage(pl,"&aReloaded player permission limits");
        SettingsGui.this.getView(pl).refresh();
      }
      return true;
    }

  }

  public class ReloadConfigItem extends ActionItem{

    protected ReloadConfigItem (ItemStack item){
      super(item);
    }

    @Override
    public boolean onClick (Player pl, ClickType click){

      if(pl.hasPermission(PermissionUtil.getAdminPermission())){
        ConfigManager config = ((OpAnvilsPlugin) SettingsGui.this.plugin).getConfigManager();
        config.loadAliases();
        config.loadConfig();
        TextUtil.sendMessage(pl,"&aReloaded configurations");
        SettingsGui.this.getView(pl).refresh();
      }



      return true;
    }

  }


}
