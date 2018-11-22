package me.varmetek.opanvils.utility;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Function;


/**
 * Class dedicated to helping display relevant information when tab complete is used on commands
 * */
public final class TabCompleteUtil
{
  private static Function<Player,String> onlinePlayer = Player::getName;
  private static Function<OfflinePlayer,String> offlinePlayer = OfflinePlayer::getName;

  /**
   * General autocomplete options for an array of strings
   *
   * @param text the text to find relevant options against
   * @param options all options available to be completed
   *
   * @returns A list of options that begin with text
   * */
  public static List<String> autoComplete(String text,String... options ){
    return autoComplete(text, Arrays.asList(options));
  }


  /**
   * Finds relevant options from the given text
   *
   * @param text the text to find relevant options against
   * @param options all options available to be completed
   * @param func the function to convert the options to a strings
   * @returns A list of options that begin with text
   * */
  public static <T> List<String> autoComplete(String text, Collection<T> options, Function<T,String> func){
    List<String> result = new ArrayList<>();
    for(T stuff: options){
      result.add(func.apply(stuff));
    }
    return autoComplete(text,result);
  }

  /**
   * Finds relevant options from the given text
   *
   * @param text the text to find relevant options against
   * @param options all options available to be completed
   * @param func the function to convert the options to a strings
   * @returns A list of options that begin with text
   * */
  public static <T>  List<String> autoComplete(String text, T[] options, Function<T,String> func){
    List<String> result = new ArrayList<>();
    for(T stuff: options){
      result.add(func.apply(stuff));
    }
    return autoComplete(text,result);
  }

  /**
   * Finds relevant options from the given text
   *
   * @param text the text to find relevant options against
   * @param options an option set
   * @param func the function to convert the options to a list of strings
   * @returns A list of options that begin with text
   * */
  public static <T> List<String> autoComplete(String text, T options, Function<T,List<String>> func){
    return autoComplete(text,func.apply(options));
  }


  /**
   * General autocomplete options for an list of strings
   *
   * @param text the text to find relevant options against
   * @param options all options available to be completed
   *
   * @returns A list of options that begin with text
   * */
  public static List<String> autoComplete(String text,List<String> options ){
    List<String> result = new ArrayList<>();
    for(String option: options){
      if(option.toLowerCase().startsWith(text)){
        result.add(option);
      }
    }

    return result;

  }



  /**
   * General autocomplete for all online players
   *
   * @param text the text to find relevant options against
   *
   * @returns A list of online players who's name beings with text
   * */
  public static List<String> autoCompleteOnlinePlayer(String text){
    return autoComplete(text, new HashSet<>(Bukkit.getOnlinePlayers()),onlinePlayer);

  }
}
