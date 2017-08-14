package me.varmetek.opanvils.utility;

import me.varmetek.opanvils.OpAnvilsPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;

import java.io.File;
import java.util.List;
import java.util.Map;

public class ConfigManager
{
  private static final String aliasesFileName =  "aliases.yml";

  private OpAnvilsPlugin plugin;
  private File aliasesFile;

  private EnchantmentLimits serverEnchants = new EnchantmentLimits();
  private EnchantmentAliases  enchantmentNames = new EnchantmentAliases();

  private boolean useServerLimits;

  public ConfigManager(OpAnvilsPlugin plugin){
    this.plugin = plugin;
    aliasesFile = new File(plugin.getDataFolder(), aliasesFileName);

  }


  public EnchantmentAliases getEnchantmentAliases(){
    return enchantmentNames;

  }

  public boolean usingGlobalLimits(){
    return useServerLimits;
  }

  public EnchantmentLimits getGlobalLimits (){
    return serverEnchants;
  }

  public void loadAliases(){
    if(!aliasesFile.exists()){
      plugin.saveResource(aliasesFileName, false);
    }
    enchantmentNames.clear();

    YamlConfiguration config = YamlConfiguration.loadConfiguration(aliasesFile);

    for(Enchantment ench: Enchantment.values()){

      List<String> names = config.getStringList(ench.getName());
      names.add(ench.getName().toLowerCase());
      enchantmentNames.addAliases(ench,names);


    }

    enchantmentNames.calculateAllNames();

  }


  public void loadConfig(){
    plugin.saveDefaultConfig();
    plugin.reloadConfig();
    FileConfiguration config = plugin.getConfig();


    useServerLimits = config.getBoolean("useGlobalLimits",true);
    if(!config.isConfigurationSection("globalLimits")) return;

    ConfigurationSection limits = config.getConfigurationSection("globalLimits");
    Map<String,Object> values =  limits.getValues(false);
    for(Map.Entry<String,Object> val : values.entrySet()){

      if(val.getValue() instanceof Number){
        int level = ((Number)val.getValue()).intValue();
        if(level<0) continue;


        Enchantment ench = enchantmentNames.getEnchantment(val.getKey());
        if(ench == null) continue;

        serverEnchants.setLimit(ench, level);
      }
    }

  }




}
