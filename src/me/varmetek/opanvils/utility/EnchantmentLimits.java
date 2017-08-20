package me.varmetek.opanvils.utility;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EnchantmentLimits
{

  public static final EnchantmentLimits NO_LIMIT;
  protected static final Integer LIMITLESS_LEVEL  = Integer.valueOf(Integer.MAX_VALUE);
  protected static final  ImmutableMap<Enchantment,Integer> LIMITLESS_REGISTRY;




   static{
    Map<Enchantment,Integer> reg =  new HashMap<>() ;
     for(Enchantment ench: Enchantment.values()){
       reg.put(ench,LIMITLESS_LEVEL);

     }

     LIMITLESS_REGISTRY = ImmutableMap.copyOf(reg);
     NO_LIMIT = new EnchantmentLimits((Void)null);
   }

  protected final Map<Enchantment,Integer> enchantments;


  public EnchantmentLimits (){
    enchantments = new HashMap<>();

  }

  public EnchantmentLimits (Map<Enchantment,Integer> enchantments){
    this.enchantments = new HashMap(enchantments);

  }

  public EnchantmentLimits (EnchantmentLimits registry){
    this.enchantments = new HashMap(registry.enchantments);

  }
  /**
   *
   * Creates a limitless enchantment registry
   */
  protected EnchantmentLimits(Void v){
    this.enchantments = LIMITLESS_REGISTRY;
  }




  public int getLimit(Enchantment enchant){
    Preconditions.checkNotNull(enchant,"Enchantment cannot be null");
    if(isLimitLess()){
      return Integer.MAX_VALUE;

    }else{
      Integer limit = enchantments.get(enchant);
      return limit == null ? enchant.getMaxLevel() : limit.intValue();
    }
  }


  public void setLimitLess(Enchantment enchant){
    checkEdit();
    Preconditions.checkNotNull(enchant,"Enchantment cannot be null");

    enchantments.put(enchant, LIMITLESS_LEVEL);


  }
  public void setLimit(Enchantment enchant, int limit){
    checkEdit();
    Preconditions.checkNotNull(enchant,"Enchantment cannot be null");
    Preconditions.checkArgument(limit >= 0,"Limit cannot be less than one");

    enchantments.put(enchant,limit);

  }

  public void unsetLimit(Enchantment enchant){
    checkEdit();
    enchantments.remove(enchant);
  }

  public void clear(){
    checkEdit();
    enchantments.clear();
  }


  public Set<Map.Entry<Enchantment,Integer>> getEnchantments(){
    return  enchantments.entrySet();
  }


  public boolean isEmpty(){
    return  enchantments.isEmpty();
  }


  public int size(){
    return enchantments.size();
  }

  public boolean contains(Enchantment enchant){
    if(isLimitLess()){
      return true;
    }else{
      Preconditions.checkNotNull(enchant, "Enchantment cannot be null");
      return enchantments.containsKey(enchant);
    }
  }


  public boolean isLimitLess(){
    return enchantments == LIMITLESS_REGISTRY;
  }


  protected  void checkEdit(){
    if(isLimitLess()){ throw new  UnsupportedOperationException("Limitless enchantments cannot be edited");}
  }




}
