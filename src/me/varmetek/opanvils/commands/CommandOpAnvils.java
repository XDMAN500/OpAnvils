package me.varmetek.opanvils.commands;

import me.varmetek.opanvils.OpAnvilsPlugin;
import me.varmetek.opanvils.utility.*;
import me.varmetek.opanvils.utility.inventorygui.SettingsGui;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.util.*;

public class CommandOpAnvils implements CommandExecutor, TabCompleter
{



  private OpAnvilsPlugin plugin;
  private PluginCommand command;
  private SettingsGui settingsGui ;

  public CommandOpAnvils(OpAnvilsPlugin plugin){
    this.plugin = plugin;
    settingsGui = new SettingsGui(plugin);
    command = plugin.getCommand("opanvils");
    command.setExecutor(this);
    command.setTabCompleter(this);
  }


  @Override
  public boolean onCommand (CommandSender sender, Command command, String label, String[] args){
    boolean permission = sender.hasPermission(PermissionUtil.getAdminPermission());
    if(args.length == 0){
      TextUtil.sendMessage(sender,"&a"+String.format(" Op Anvils( %s ) - Created by Varmetek ",plugin.getDescription().getVersion()));
      if(permission){
        TextUtil.sendMessage(sender, "&a  /"+label+" ? to change the settings");
        TextUtil.sendMessage(sender, "&a  /"+label+" gui to view the gui");

      }
      return true;
    }

    if(!permission){
      TextUtil.sendMessage(sender,"&c  You do not have permission for this command");
      return false;
    }

    switch (args[0].toLowerCase()){
      case "help":
      case "?":{
        String prefix1  = "&e > &a/"+label+" ";
        String prefix2 = "&7    ";

        TextUtil.sendMessage(sender,
          " ",
          "&aOp Anvils Admin Help Menu",
          prefix1 + "reload",
          prefix2 + "Reloads the plugin settings",
          prefix1 + "mode",
          prefix2 + "Check what limit mode is used.",
          prefix1 + "enchantment <enchantment>",
          prefix2 + "Displays information about an enchantment",
          prefix1 + "check <player>",
          prefix2 + "Checks the enchantment limit for a player",
          " "

          );
      }break;

      case "reload":{
        if(args.length == 1){
          TextUtil.sendMessage(sender, "&aReloading OpAnvils");
          TextUtil.sendMessage(sender, "&7  Do '/" + label + " " + args[0] + " ?' for more info");
          try {
            plugin.getConfigManager().loadAliases();
            plugin.getConfigManager().loadConfig();
            plugin.getPlayerHandler().loadAllPlayers();
          } catch (RuntimeException ex) {
            TextUtil.sendMessage(sender, "&c Error:" + ex.getMessage());
            return false;
          }
          TextUtil.sendMessage(sender, "&aOpAnvils reloaded");
        }else{
          switch(args[1].toLowerCase()){
            case "?":
            case "help":{
              TextUtil.sendMessage(sender,
                "&aOpAnvils Reload Help",
                String.format("&e   /%s %s %s",label,args[0],"permission"),
                "&7   Reload permission limits",
                String.format("&e   /%s %s %s",label,args[0],"config"),
                "&7   Reload config file",
                String.format("&e   /%s %s %s",label,args[0],"names"),
                "&7   Reload enchantment names"
              );

            }break;

            case "perm":
            case "perms":
            case "permission":
            case "permissions":{
              try{
                plugin.getPlayerHandler().loadAllPlayers();
                TextUtil.sendMessage(sender,"&a Permission limits reloaded");
              }catch(RuntimeException ex){
                ex.printStackTrace();
                TextUtil.sendMessage(sender,"&cAn error occured: "+ ex.getMessage() );
                return false;
              }


            }break;


            case "config":
            case "limits":
            case "conf": {
              try{
                plugin.getConfigManager().loadConfig();
                TextUtil.sendMessage(sender,"&a Config reloaded");
              }catch(RuntimeException ex){
                ex.printStackTrace();
                TextUtil.sendMessage(sender,"&cAn error occured: "+ ex.getMessage() );
                return false;
              }


            }break;


            case "names":
            case "aliases": {
              try{
                plugin.getConfigManager().loadAliases();
                TextUtil.sendMessage(sender,"&a Enchantment names reloaded");
              }catch(RuntimeException ex){
                ex.printStackTrace();
                TextUtil.sendMessage(sender,"&cAn error occured: "+ ex.getMessage() );
                return false;
              }


            }break;

            default:{
              TextUtil.sendMessage(sender,"&c Unknown reload option");
              TextUtil.sendMessage(sender, "&c  Do '/" + label + " " + args[0] + " ?' for help");
            }break;

          }
        }

      }break;
      case "ench":
      case "enchant":
      case "enchantment":{

        if(args.length == 1){
          TextUtil.sendMessage(sender,"&a Used to find more information about an enchantment");
          TextUtil.sendMessage(sender, "&7   "+String.format("/%s %s [enchantmntent]",label,args[0]));
          TextUtil.sendMessage(sender, "&a Use "+String.format("&7/%s %s -all &ato list all enchantments",label,args[0]) );
          return false;
        }
        EnchantmentAliases names = plugin.getConfigManager().getEnchantmentAliases();
        if(args[1].startsWith("-")){
          TextUtil.sendMessage(sender,"&a Listing all enchantments");
          TextUtil.sendMessage(sender, new ArrayList<>(names.getAllAliases()).toString());
          TextUtil.sendMessage(sender);
          return true;
        }


        Enchantment ench = names.getEnchantment(args[1]);
        if(ench == null){
          TextUtil.sendMessage(sender,"&cUnknown enchantment "+ args[1]);
          return false;
        }

        TextUtil.sendMessage(sender,
          "&aViewing Enchantment &7- &e&o"+ names.filterName(args[1]),
          "  &aBukkit Name: &e"+ ench.getName(),
          "  &aGlobal Limit: &e"+plugin.getConfigManager().getGlobalLimits().getLimit(ench),
          "  &aAliases:"
          );

        for(String name : names.getAliases(ench)){
          TextUtil.sendMessage(sender,"&7   - "+ name);
        }

      }break;


      case "mode":
      case "limitmode":{
        if(args.length <= 1){
          boolean using = plugin.getConfigManager().usingGlobalLimits();
          TextUtil.sendMessage(sender,String.format("&a  Currently using &e&o%s&a limits" , using ?"Global" : "Permission" ));
        }

      }break;
      case "check":
      case "checklimit":{

        if(args.length == 1){
          TextUtil.sendMessage(sender,
            "&a Check the enchantment limits of a player",
            "&7   /"+label+" "+ args[0]+ " [player]",
            "&a Check the global enchantment Limits",
            "&7   /"+label+" "+ args[0]+ " -global"
          );
          return false;
        }

        EnchantmentLimits limits;
        Player player = null;
        if(args[1].startsWith("-")){
          TextUtil.sendMessage(sender,"&a Enchantment Limits of (&e&o-Global)");
          limits = plugin.getConfigManager().getGlobalLimits();
        }else {
          player = Bukkit.getPlayer(args[1]);
          if (player == null){
            TextUtil.sendMessage(sender, "&c Unknown player " + args[1]);
            return false;
          }
          boolean global = plugin.getConfigManager().usingGlobalLimits();
          boolean bypass = player.hasPermission(PermissionUtil.getServerBypass());

          TextUtil.sendMessage(sender, String.format("&a Enchantment Limits of (&e&o%s)", player.getName()));

          if (bypass){
            TextUtil.sendMessage(sender, "&7 This player has the bypass permission and therefor has no limits");
            return true;
          }

           limits = plugin.getPlayerHandler().getLimits(player);



          if (global){
            TextUtil.sendMessage(sender, "&7 Global limits are enabled, thus global limits are shown");
          }
        }



        Set<Map.Entry<Enchantment,Integer>> enchants = limits.getEnchantments();
        if (enchants.isEmpty()){
          TextUtil.sendMessage(sender, "  &7 No limits have been changed");
          return true;
        }
        EnchantmentAliases aliases =plugin.getConfigManager().getEnchantmentAliases();
        for (Map.Entry<Enchantment,Integer> ench : enchants) {
          TextUtil.sendMessage(sender, String.format("  &8- &7%s [%d]",aliases.getPrimaryName(ench.getKey()), ench.getValue()));
        }
        TextUtil.sendMessage(sender, " ");

        TextUtil.sendMessage(sender, "&a All enchantments not shown are set to default limits");
        TextUtil.sendMessage(sender, " ");

      }break;





      case "gui":{
        if(!(sender instanceof Player)){
          TextUtil.sendMessage(sender,"&cYou must be a player to use the gui");

          return false;
        }

        Player player = (Player)sender;
        settingsGui.open(player);

      }break;

      default:{
        TextUtil.sendMessage(sender,"&cUnknown subcommand. Do /"+label+" ? for help");
      }break;



    }



    return true;
  }

  @Override
  public List<String> onTabComplete (CommandSender sender, Command command, String alias, String[] args){
    boolean permission = sender.hasPermission(PermissionUtil.getAdminPermission());
    if(!permission) return Collections.EMPTY_LIST;

    if(args.length ==0){
      return Collections.EMPTY_LIST;
    }

    if(args.length == 1){
      return TabCompleteUtil.autoComplete(args[0], "?","reload","enchantment","mode","check","gui");

    }

    switch (args[0].toLowerCase()){
      case "ench":
      case "enchant":
      case "enchantment":{
        if(args.length == 2){
          List<String> options = new ArrayList<>(plugin.getConfigManager().getEnchantmentAliases().getAllAliases());
          options.add("-all");
          return options;
        }
      }
      case "check":{
          if(args.length == 2 ){
          List<String> options = TabCompleteUtil.autoCompleteOnlinePlayer(args[1]);
          options.add("-global");
          return options;
          }
      }

      case "reload":{
        if(args.length == 2 ){
          return TabCompleteUtil.autoComplete(args[1],"perms","config","names");
        }
      }

    }


    return Collections.EMPTY_LIST;
  }
}


