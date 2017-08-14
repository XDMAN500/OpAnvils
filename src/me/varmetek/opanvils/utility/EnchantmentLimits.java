package me.varmetek.opanvils.utility;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EnchantmentLimits
{
  public static final Integer LIMITLESS_LEVEL  = Integer.valueOf(Integer.MAX_VALUE);
  public static final Map<Enchantment,Integer> LIMITLESS_REGISTRY;



   static{
    Map<Enchantment,Integer> reg =  new HashMap<>() ;
     for(Enchantment ench: Enchantment.values()){
       reg.put(ench,LIMITLESS_LEVEL);

     }

     LIMITLESS_REGISTRY = ImmutableMap.copyOf(reg);
   }

  private final Map<Enchantment,Integer> enchantments;

  public EnchantmentLimits (){
    enchantments = new HashMap<>();
  }

  public EnchantmentLimits (Map<Enchantment,Integer> enchantments){
    this.enchantments = new HashMap(enchantments);
  }

  public EnchantmentLimits (EnchantmentLimits registry){
    this.enchantments = new HashMap(registry.enchantments);
  }


  public int getLimit(Enchantment enchant){
    Preconditions.checkNotNull(enchant,"Enchantment cannot be null");
    Integer limit = enchantments.get(enchant);
    return limit == null ? enchant.getMaxLevel() : limit.intValue();
  }


  public void setLimitLess(Enchantment enchant){
    Preconditions.checkNotNull(enchant,"Enchantment cannot be null");
    enchantments.put(enchant,LIMITLESS_LEVEL);

  }
  public void setLimit(Enchantment enchant, int limit){
    Preconditions.checkNotNull(enchant,"Enchantment cannot be null");
    Preconditions.checkArgument(limit >= 0,"Limit cannot be less than one");
    enchantments.put(enchant,limit);

  }

  public void unsetLimit(Enchantment enchant){

    enchantments.remove(enchant);
  }

  public void clear(){
    enchantments.clear();
  }


  public Set<Map.Entry<Enchantment,Integer>> getEnchantments(){
    return enchantments.entrySet();
  }


  public boolean isEmpty(){
    return enchantments.isEmpty();
  }


  public int size(){
    return enchantments.size();
  }

  public boolean contains(Enchantment enchant){
    Preconditions.checkNotNull(enchant,"Enchantment cannot be null");
    return enchantments.containsKey(enchant);
  }



}
