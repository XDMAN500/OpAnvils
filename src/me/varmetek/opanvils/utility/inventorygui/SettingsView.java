package me.varmetek.opanvils.utility.inventorygui;

import com.sun.xml.internal.ws.util.StringUtils;
import me.varmetek.opanvils.inventorygui.GuiView;
import me.varmetek.opanvils.OpAnvilsPlugin;
import me.varmetek.opanvils.utility.ConfigManager;
import me.varmetek.opanvils.utility.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class SettingsView extends GuiView<Inventory>
{

  private static final Comparator<Enchantment> compare =Comparator.nullsLast(Comparator.comparingInt((ench) -> ench.getItemTarget() == null ? Enchantment.values().length: ench.getItemTarget().ordinal()));

  protected OpAnvilsPlugin plugin;
  public SettingsView (SettingsGui handle, Player player){
    super(handle, player);
    this.plugin = (OpAnvilsPlugin)handle.getPlugin();
    refresh();
  }

  @Override
  public Inventory createInventory (){
    return Bukkit.createInventory(this,9*5,"Settings");
  }

  @Override
  public void refresh (){
    inventory.clear();
    ItemStack blank = new ItemStack(Material.STAINED_GLASS_PANE,1, DyeColor.BLACK.getWoolData());
    for(int i = 0; i< 9; i++){
      inventory.setItem(i,blank);
    }
    boolean global = plugin.getConfigManager().usingGlobalLimits();

    inventory.setItem(0, ((SettingsGui)handle).reloadPlayerItem.toItemStack());
    inventory.setItem(4,getModeItem(global));
    inventory.setItem(8, ((SettingsGui)handle).reloadConfigItem.toItemStack());
    List<Enchantment> sorted = Arrays.asList(Enchantment.values());
    sorted.sort(compare);
    for(Enchantment ench: sorted){
      inventory.addItem(getEnchantmentItem(ench));
    }
  }

  @Override
  public boolean onClickDefault (InventoryClickEvent ev){
    return true;
  }

  @Override
  public boolean onClickOutside (){
    return true;
  }

  @Override
  public boolean onInvOpen (){
    return false;
  }

  @Override
  public boolean onInvClose (){
    return false;
  }

  private  ItemStack getModeItem(boolean global){
    ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE,1 , (global ? DyeColor.YELLOW :DyeColor.RED).getWoolData());
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(TextUtil.color(String.format("&aLimitMode: &e&o%s",global ? "Global" : "Permission")));
    item.setItemMeta(meta);

    return item;
  }



  public Material targetToMaterial(EnchantmentTarget targ){
    if(targ == null) return Material.PUMPKIN;
    switch (targ) {

      case ALL:
        return Material.BOOK;
      case ARMOR:
        return Material.IRON_CHESTPLATE;
      case ARMOR_FEET:
        return Material.DIAMOND_BOOTS;
      case ARMOR_LEGS:
        return Material.DIAMOND_LEGGINGS;
      case ARMOR_TORSO:
        return Material.DIAMOND_CHESTPLATE;
      case ARMOR_HEAD:
        return Material.DIAMOND_HELMET;
      case WEAPON:
        return Material.DIAMOND_SWORD;
      case TOOL:
        return Material.DIAMOND_PICKAXE;
      case BOW:
        return Material.BOW;
      case FISHING_ROD:
        return Material.FISHING_ROD;
      case BREAKABLE:
        return Material.IRON_SPADE;
      default:
        return Material.PUMPKIN;
    }

  }

  private String capitalize(String s){
    String[] parts = s.split("_");
    StringBuilder sb = new StringBuilder();
    for(String p: parts){
      sb.append(StringUtils.capitalize(p)).append("_");
    }
    return sb.substring(0,sb.length()-1).toString();
  }

  private ItemStack getEnchantmentItem(Enchantment ench){

    ConfigManager config = plugin.getConfigManager();
    EnchantmentTarget target = ench.getItemTarget();
    Material material = targetToMaterial(target);
    ItemStack item = new ItemStack(material);
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(TextUtil.color("&a"));
    meta.addEnchant(ench,1,true);
    List<String> lore = new ArrayList<>();
    lore.add(TextUtil.color("&a"));
    lore.add(TextUtil.color("&aBukkit Name:&e "+ ench.getName()));
    lore.add(TextUtil.color("&aMax Level:&e "+ config.getGlobalLimits().getLimit(ench)));
    lore.add(TextUtil.color("&aAliases:"));
    for(String name: config.getEnchantmentAliases().getAliases(ench)){
      lore.add(TextUtil.color("  &a- &e"+name));
    }

    meta.setLore(lore);
    item.setItemMeta(meta);
    return item;

  }



}
