package me.varmetek.opanvils.listener;

import com.google.common.base.Preconditions;
import me.varmetek.opanvils.OpAnvilsPlugin;
import me.varmetek.opanvils.utility.EnchantmentLimits;
import me.varmetek.opanvils.utility.PermissionUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

import java.util.*;

public class AnvilListener implements Listener
{
  private OpAnvilsPlugin plugin;

 // private Set<Player> enhanced = new HashSet<>();

  public  AnvilListener(OpAnvilsPlugin plugin){
    this.plugin = plugin;
  }




  @EventHandler
  public void anvilEvent(PrepareAnvilEvent ev){
    Inventory inv = ev.getInventory();

    if(inv.getViewers().isEmpty()) return;
    Player player = (Player)inv.getViewers().get(0);//Get first viewer, usually player
    if(!player.hasPermission(PermissionUtil.getFeatureAccess())){
      return; //Skip if no permission
    }

    ItemStack result = ev.getResult();
    if(!hasEnchantments(result))return; //Quick way to isolate only work with vanilla item combining
    ItemStack primary = inv.getItem(0);
    ItemStack secondary = inv.getItem(1);
    if(isAir(primary ) || isAir(secondary)) return; // Combine non enchanted item


    if(!(hasEnchantments(primary) || hasEnchantments(secondary))) return;






    Map<Enchantment,Integer> enchant1 =  getEnchantments(primary);

    Map<Enchantment,Integer> enchant2 =  getEnchantments(secondary);

    ItemMeta meta = resetCost(result);
    resetCost(primary);
    resetCost(secondary);

    boolean canBypass = player.hasPermission(PermissionUtil.getLimitBypass());

    EnchantmentLimits playerLimits = plugin.getPlayerHandler().getLimits(player);

    Map<Enchantment,Integer> enchants = combineEnchants(enchant1,enchant2,canBypass,playerLimits);

    if(meta instanceof EnchantmentStorageMeta){
      EnchantmentStorageMeta emeta = (EnchantmentStorageMeta)meta;
      for(Enchantment ench: emeta.getStoredEnchants().keySet()){
       emeta.removeStoredEnchant(ench);

      }

      for(Map.Entry<Enchantment,Integer> entry : enchants.entrySet()){
        emeta.addStoredEnchant(entry.getKey(),entry.getValue().intValue(),true);

      }

    }else{

      //Remove old enchantments
      for(Enchantment ench: meta.getEnchants().keySet()){
        meta.removeEnchant(ench);
      }


      //Add new enchantments
      for(Map.Entry<Enchantment,Integer> entry : enchants.entrySet()){

        //Add only if fitting for item
        if(!entry.getKey().getItemTarget().includes(result.getType())) continue;
        if(meta.hasConflictingEnchant(entry.getKey()) ) continue;

        meta.addEnchant(entry.getKey(), entry.getValue().intValue(), true);


      }
    }

    result.setItemMeta(meta);
    ev.setResult(result);
  }



  public static boolean isAir(ItemStack item){
    return item == null || item.getType() == Material.AIR;
  }

  /**
   * Determines whether this item can be used for custom enchanting
   *
   * Returns true if the item has enchantment
   *
   * **/
  public static boolean hasEnchantments(ItemStack item) {
    if (!isAir(item) && item.hasItemMeta()) {
      ItemMeta meta = item.getItemMeta();
      if (meta instanceof EnchantmentStorageMeta) {
        return ((EnchantmentStorageMeta) meta).hasStoredEnchants();
      } else return meta.hasEnchants();


    }else return false;

  }


  public static Map<Enchantment,Integer> getEnchantments(ItemStack item){
    Preconditions.checkArgument(!isAir(item), "Air cannot have enchantments");

    if (item.hasItemMeta()) {
      ItemMeta meta = item.getItemMeta();
      if (meta instanceof EnchantmentStorageMeta) {
        return ((EnchantmentStorageMeta) meta).getStoredEnchants();
      } else return meta.getEnchants();


    }else return new HashMap<>();
  }


  /**
   * Attempts to combine two separate enchantment data into one
   *
   * Returns the combined enchantments and levels
   *
   * **/
  public  static Map<Enchantment,Integer> combineEnchants(Map<Enchantment,Integer> itemOne, Map<Enchantment,Integer> itemTwo, boolean canBypass, EnchantmentLimits limits){
    Preconditions.checkNotNull(itemOne);
    Preconditions.checkNotNull(itemTwo);
;


    Set<Enchantment> avaliable = new LinkedHashSet<>(itemOne.keySet());
    avaliable.addAll(itemTwo.keySet());

    Map<Enchantment,Integer> used = new LinkedHashMap<>();
    boolean canSkip =canBypass || limits == null ;

    for(Enchantment ench: avaliable){
      Integer one = itemOne.get(ench);
      Integer two = itemTwo.get(ench);
      int result = merge(one,two);
      if(!canSkip){
        result = Math.min(limits.getLimit(ench),result);
      }

      if(result <=0) continue;

      used.put(ench,result);
    }

    return used;
  }


  //Merges two integer numbers if both are non zero.
  //
  //Merging means that when the numbers are equal,
  // the result is one greater than then the value of the first number.
  // if the numbers are inequal, the largest number is returned.
  //   if both numbers are 0, 0 is returned.
  //
  private static int merge(Integer one,Integer two){
    int val1 = one == null ? 0 : one.intValue();
    int val2 = two == null? 0 : two.intValue();
    return merge(val1,val2);
  }

  //Merges two integer numbers if both are non zero.
  //
  //Merging means that when the numbers are equal,
  // the result is one greater than then the value of the first number.
  // if the numbers are inequal, the largest number is returned.
  //   if both numbers are 0, 0 is returned.
  //
  private static int merge(int one,int two){
    if(one <= 0 && two <= 0) return 0;

    return one == two ? one+1 : Math.max(one,two);
  }

  /**
   * Resets the repair cost of an item to 0 to allow for infinite anvil use
   * **/
  public static ItemMeta resetCost(ItemStack stack){
    Preconditions.checkNotNull(stack,"Item cannot be null");

    ItemMeta meta =  stack.getItemMeta();
    if(stack.hasItemMeta()) {
      if (!(meta instanceof Repairable)) {
        return meta;
      }

      ((Repairable) meta).setRepairCost(0);
      stack.setItemMeta(meta);
    }
    return meta;

  }
}
