package me.varmetek.opanvils.utility;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.permissions.Permission;

import static com.google.common.base.Preconditions.checkNotNull;

public final class PermissionUtil
{

  //Can't use reflection now :)))
  private PermissionUtil(){throw new UnsupportedOperationException("Cannot instantiate a new instance of PermissionUtil");}


  private static Permission allEnchantments;
  private static Permission limitBypass;
  private static Permission allPermissions;
  private static Permission adminPermission;
  private static Permission featureAccess;



  public static void init(Server server, ConfigManager config){

    checkNotNull(server,"Server cannot be null");
    allEnchantments = getPerm(server,"opanvils.enchantment.*");
    limitBypass = getPerm(server,"opanvils.bypass");
    allPermissions = getPerm(server,"opanvils.*");
    adminPermission = getPerm(server,"opanvils.admin");
    featureAccess = getPerm(server,"opanvils.use");

    for(String ench : config.getEnchantmentAliases().getAllAliases()){
      Permission perm = new Permission("opanvils.enchantment."+ench+".*");
      perm.addParent(allEnchantments,true);
      server.getPluginManager().addPermission(perm);
    }
  }

  private static Permission getPerm(Server server, String perm){
    return checkNotNull(server.getPluginManager().getPermission(perm),"Cannot find permission "+ perm);
  }


  public static Permission getPermissionFor(Enchantment ench){
    checkNotNull(ench,"Enchantment cannot be null");
    return Bukkit.getPluginManager().getPermission("opanvils.enchantment."+ench.getName()+".*");
  }

  public static Permission getAllEnchantments(){
    return allEnchantments;
  }

  public static Permission getAllPermissions(){
    return allPermissions;
  }

  public static Permission getLimitBypass(){
    return limitBypass;
  }

  public static Permission getAdminPermission(){
    return adminPermission;
  }

  public static Permission getFeatureAccess(){
    return featureAccess;
  }


  public static void dispose(){
    allEnchantments = null;
  }





}
