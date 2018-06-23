package me.varmetek.opanvils.utility;

import me.varmetek.opanvils.OpAnvilsPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ConfigManager
{
  public static final String PREFIX = "&aOPAnvils&8>> &a";
  private static final String aliasesFileName =  "aliases.yml";
  private static final int configVersion = 2;

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
    int ver = config.getInt("version",0);


    useServerLimits = config.getBoolean("useGlobalLimits",true);



    if(config.isConfigurationSection("globalLimits")){
      ConfigurationSection limits = config.getConfigurationSection("globalLimits");
      Map<String,Object> values = limits.getValues(false);
      for (Map.Entry<String,Object> val : values.entrySet()) {

        if (val.getValue() instanceof Number){
          int level = ((Number) val.getValue()).intValue();
          if (level < 0) continue;


          Enchantment ench = enchantmentNames.getEnchantment(val.getKey());
          if (ench == null) continue;

          serverEnchants.setLimit(ench, level);
        }
      }
    }

    if(ver > configVersion){
      plugin.sendConsole(PREFIX + "Config is from a future version of this plugin and may not work properly");
    }else if(ver < configVersion){
      plugin.sendConsole();
      plugin.sendConsole(PREFIX +"Config is outdated!");
      plugin.getLogger().info("Creating updated file");


      InputStream in =    plugin.getResource("config.yml");
      if (in == null) {
        plugin.getLogger().severe("Could not find internal config. Aborting");
        return;
      }

      File outFile = new File(plugin.getDataFolder(), "config.yml.new");

      if (!plugin.getDataFolder().exists()) {
        plugin.getDataFolder().mkdirs();
      }

      try {

        OutputStream out = new FileOutputStream(outFile);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
          out.write(buf, 0, len);
        }
        out.close();
        in.close();

      } catch (IOException ex) {
        plugin.getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
      }
      plugin.sendConsole(
        PREFIX +"Updated file 'config.yml.new' created",
        PREFIX +"To apply changes just copy data from the old config file to the new config",
        PREFIX + "Rename config.yml.new to config.yml to update"
      );


    }

  }




}
