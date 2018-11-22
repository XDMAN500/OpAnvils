package me.varmetek.opanvils.utility;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.bukkit.enchantments.Enchantment;

import java.util.*;
import java.util.regex.Pattern;


/**
 * Handles all the various names to attribute to an enchantment
 * **/
public class EnchantmentAliases
{
  private static final ImmutableMap<String,Enchantment> EMPTY_MAP = ImmutableMap.of();
  private Map<Enchantment,List<String>> names = new HashMap<>();
  private ImmutableMap<String,Enchantment>  flatMap = EMPTY_MAP;


  /***
   *
   * Add aliases to an enchantment
   * */
  public void addAliases(Enchantment ench,String...aliases){
    Preconditions.checkNotNull(ench,"Enchantment cannot be null");
    if(aliases.length == 0) return;

    List<String> nameSet = names.get(ench);
    if(nameSet == null){nameSet = new ArrayList<>();}


    for(String al : aliases){
      try{
        String filtered = filterName(al);
        if(nameSet.contains(filtered)) continue;
        nameSet.add(filtered);
      }catch(NullPointerException | IllegalArgumentException ex){
        continue;
      }
    }
    names.put(ench,nameSet);
  }

  /***
   *
   * Removes aliases from an enchantment
   * */
  public void removeAliases(Enchantment ench,String...aliases){
    Preconditions.checkNotNull(ench,"Enchantment cannot be null");
    if(aliases.length == 0) return;

    List<String> nameSet = names.get(ench);
    if(nameSet == null){nameSet = new ArrayList<>();}
    for(String al : aliases){
      try{
        nameSet.remove(filterName(al));
      }catch(NullPointerException | IllegalArgumentException ex){
        continue;
      }
    }
    names.put(ench,nameSet);
  }


  /***
   *
   * Add aliases to an enchantment
   * */
  public void addAliases(Enchantment ench, Collection<String> aliases){
    Preconditions.checkNotNull(ench,"Enchantment cannot be null");
    if(aliases.isEmpty()) return;

    List<String> nameSet = names.get(ench);
    if(nameSet == null){nameSet = new ArrayList<>();}
    for(String al : aliases){
      try{
        String filtered = filterName(al);
        if(nameSet.contains(filtered)) continue;
        nameSet.add(filtered);
      }catch(NullPointerException | IllegalArgumentException ex){
        continue;
      }
    }

    names.put(ench,nameSet);
  }


  /***
   *
   * Removes aliases from an enchantment
   * */
  public void removeAliases(Enchantment ench,  Collection<String> aliases){
    Preconditions.checkNotNull(ench,"Enchantment cannot be null");
    if(aliases.isEmpty()) return;

    List<String> nameSet = names.get(ench);
    if(nameSet == null){nameSet = new ArrayList<>();}
    for(String al : aliases){
      try{
        nameSet.remove(filterName(al));
      }catch(NullPointerException | IllegalArgumentException ex){
        continue;
      }
    }

    names.put(ench,nameSet);
  }


  /***
   * Attempts to get an enchantment by a name.
   * First attempts to match the natural names and then the aliases.
   *
   * If no enchantment can be found, null is returned.
   *
   * */
  public Enchantment getEnchantment(String name){
    String fName = filterName(name);
    Enchantment enchant =  Enchantment.getByName(fName.toUpperCase());
    if(enchant == null){
      enchant = flatMap.get(fName);
    }



    return enchant;
  }


  /***
   *
   * Returns all the aliases for a specific enchantment
   * */
  public List<String> getAliases(Enchantment ench){
    Preconditions.checkNotNull(ench,"Enchantment cannot be null");
    List<String> result =  names.get(ench);
    return result == null? Collections.emptyList() :Collections.unmodifiableList(names.get(ench));
  }

  /***
   * Gets the first alias if it exists.
   * If not, returns the enchantments natural name.
   *
   * */
  public String getPrimaryName(Enchantment ench){
    List<String> names = getAliases(ench);
    if(names == null || names.isEmpty()){
      return ench.getName().toLowerCase();
    }else{
      return names.get(0);
    }
  }


  /**
   * Clears all aliases for all enchantments
   * **/
  public void clear(){
    names.clear();
    flatMap =EMPTY_MAP;

  }


  /**
   * Places all name aliases into a fast -direct
   * string to enchantment map
   *
   * **/
  public void calculateAllNames(){
    Map<String,Enchantment> temp = new HashMap<>();
    for (Map.Entry<Enchantment,List<String>> aliases : names.entrySet()) {
      if (aliases.getValue() == null){
        continue;
      }


      for(String name: aliases.getValue()){

          temp.put(name, aliases.getKey());

      }
    }


   flatMap = ImmutableMap.copyOf(temp);
  }

  public Map<String,Enchantment> getFlatMap(){

    return flatMap;
  }


  public Set<String> getAllAliases(){

    return flatMap.keySet();
  }

  private static final Pattern _ToUnderscore = Pattern.compile("[\\-\\s]");
  private static final Pattern _Illegal = Pattern.compile("\\W");

  public String filterName(String name){
    Preconditions.checkNotNull(name,"Name cannot be null");
    Preconditions.checkArgument(name.length() > 3,"Name must be longer than 3 characters");
    String filtered = name.trim().toLowerCase();


    // Turn whitespace and hyphens into an underscore
    filtered = _ToUnderscore.matcher(filtered).replaceAll("_");


    //Remove numbers and special characters
    filtered = _Illegal.matcher(filtered).replaceAll("");

    return filtered;
  }





}
