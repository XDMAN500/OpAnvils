package me.varmetek.opanvils.utility;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.bukkit.enchantments.Enchantment;

import java.util.*;
import java.util.regex.Pattern;

public class EnchantmentAliases
{
  private static final ImmutableMap<String,Enchantment> EMPTY_MAP = ImmutableMap.of();
  private Map<Enchantment,List<String>> names = new HashMap<>();
  private ImmutableMap<String,Enchantment>  flatMap = EMPTY_MAP;



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


  public Enchantment getEnchantment(String name){
    String fName = filterName(name);
    Enchantment enchant =  Enchantment.getByName(fName.toUpperCase());
    if(enchant == null){
      enchant = flatMap.get(fName);
    }



    return enchant;
  }


  public List<String> getAliases(Enchantment ench){
    Preconditions.checkNotNull(ench,"Enchantment cannot be null");
    List<String> result =  names.get(ench);
    return result == null? Collections.emptyList() :Collections.unmodifiableList(names.get(ench));
  }


  public String getPrimaryName(Enchantment ench){
    List<String> names = getAliases(ench);
    if(names == null || names.isEmpty()){
      return ench.getName().toLowerCase();
    }else{
      return names.get(0);
    }
  }

  public void clear(){
    names.clear();
    flatMap =EMPTY_MAP;

  }


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
