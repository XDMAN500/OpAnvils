package me.varmetek.opanvils.utility;


import com.google.common.base.Preconditions;
import me.varmetek.opanvils.OpAnvilsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerHandler implements Listener
{

  private static final Pattern permission = Pattern.compile("(\\w+)\\.(\\*|\\d+)");
  private static final String root = "opanvils.enchantment.";

  private ConcurrentMap<Player,EnchantmentLimits> playerEnchants = new ConcurrentHashMap<>();
  private OpAnvilsPlugin plugin;

  public PlayerHandler(OpAnvilsPlugin plugin){
    this.plugin = Preconditions.checkNotNull(plugin,"Plugin Cannot be null");
    Bukkit.getPluginManager().registerEvents(this,plugin);
  }



  public void loadAllPlayers(){
    for(Player player: Bukkit.getOnlinePlayers()){
      loadLimits(player);
    }
  }

  public EnchantmentLimits loadLimits(Player player){

    EnchantmentLimits result;
    if(player.hasPermission(PermissionUtil.getAllEnchantments())){
      result = EnchantmentLimits.NO_LIMIT;

    }else {
      result = new EnchantmentLimits();


      EnchantmentAliases aliases = plugin.getConfigManager().getEnchantmentAliases();
      Set<PermissionAttachmentInfo> perms = player.getEffectivePermissions();

      for (PermissionAttachmentInfo perm : perms) {

        if (!perm.getValue()) continue;
        if (!perm.getPermission().startsWith(root)) continue;

        String node = perm.getPermission().toLowerCase().substring(root.length());
        Matcher match = permission.matcher(node);
        if (!match.matches()) continue;

        Enchantment ench = aliases.getEnchantment(match.group(1));
        if (ench == null) continue;

        if (match.group(2).equals("*")){
          result.setLimitLess(ench);
        } else {
          Integer number = null;
          try {
            number = Integer.parseInt(match.group(2));
          } catch (NumberFormatException ex) {
            //Ignore
          } finally {
            if (number == null){
              continue;
            }
          }

          result.setLimit(ench, number.intValue());

        }

      }
    }

    playerEnchants.put(player,result);
    return result;
  }


  public EnchantmentLimits getLimits(Player player){

     if(player.hasPermission(PermissionUtil.getLimitBypass())){
      return  EnchantmentLimits.NO_LIMIT;
    }else  if(plugin.getConfigManager().usingGlobalLimits()){
       return new EnchantmentLimits(plugin.getConfigManager().getGlobalLimits());
     }else {

       EnchantmentLimits result = playerEnchants.get(player);

      if (result == null){
        result = loadLimits(player);
      }
       return result;

    }

  }

  @EventHandler
  public void loginEvent(PlayerJoinEvent ev){
    if(!plugin.getConfigManager().usingGlobalLimits()){
      loadLimits(ev.getPlayer());
    }
  }

  @EventHandler
  public void leaveEvent(PlayerQuitEvent ev){
      EnchantmentLimits reg = playerEnchants.get(ev.getPlayer());
      if(reg != null){
        reg.clear();
      }

      playerEnchants.remove(ev.getPlayer());
  }

  @EventHandler
  public void kickEvent(PlayerKickEvent ev){
    EnchantmentLimits reg = playerEnchants.get(ev.getPlayer());
    if(reg != null){
      reg.clear();
    }

    playerEnchants.remove(ev.getPlayer());
  }


  public void clear(){
    for(Map.Entry<Player,EnchantmentLimits> entry: playerEnchants.entrySet()){
      if(entry.getValue().isLimitLess()) return;
        entry.getValue().clear();
        playerEnchants.remove(entry.getKey());
    }
  }


  public void dispose(){
   clear();
   playerEnchants = null;
  }

}
