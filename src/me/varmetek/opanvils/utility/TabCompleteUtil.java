package me.varmetek.opanvils.utility;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Function;

public final class TabCompleteUtil
{
  private static Function<Player,String> onlinePlayer = Player::getName;
  private static Function<OfflinePlayer,String> offlinePlayer = OfflinePlayer::getName;

  public static List<String> autoComplete(String text,String... options ){
    return autoComplete(text, Arrays.asList(options));
  }

  public static <T> List<String> autoComplete(String text, Collection<T> options, Function<T,String> func){
    List<String> result = new ArrayList<>();
    for(T stuff: options){
      result.add(func.apply(stuff));
    }
    return autoComplete(text,result);
  }

  public static <T>  List<String> autoComplete(String text, T[] options, Function<T,String> func){
    List<String> result = new ArrayList<>();
    for(T stuff: options){
      result.add(func.apply(stuff));
    }
    return autoComplete(text,result);
  }

  public static <T> List<String> autoComplete(String text, T options, Function<T,List<String>> func){
    return autoComplete(text,func.apply(options));
  }

  public static List<String> autoComplete(String text,List<String> options ){
    List<String> result = new ArrayList<>();
    for(String option: options){
      if(option.toLowerCase().startsWith(text)){
        result.add(option);
      }
    }

    return result;

  }


  public static List<String> autoCompleteOnlinePlayer(String text){
    return autoComplete(text, new HashSet<>(Bukkit.getOnlinePlayers()),onlinePlayer);

  }
}
