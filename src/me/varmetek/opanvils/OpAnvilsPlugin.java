package me.varmetek.opanvils;

import me.varmetek.opanvils.commands.CommandOpAnvils;
import me.varmetek.opanvils.listener.AnvilListener;
import me.varmetek.opanvils.utility.ConfigManager;
import me.varmetek.opanvils.utility.PermissionUtil;
import me.varmetek.opanvils.utility.PlayerHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class OpAnvilsPlugin extends JavaPlugin
{



  private PlayerHandler playerHandler;
  private ConfigManager configManager;





  @Override
  public void onLoad() {




  }

  @Override
  public void onEnable() {


    configManager = new ConfigManager(this);
    configManager.loadAliases();
    configManager.loadConfig();
    PermissionUtil.init(this.getServer(),configManager);
    playerHandler = new PlayerHandler(this);


    registerListeners();
    registerCommands();
    Bukkit.getScheduler().runTask(this, ()->{playerHandler.loadAllPlayers();});

  }

  @Override
  public void onDisable() {

    PermissionUtil.dispose();
    playerHandler.dispose();
    configManager = null;
    playerHandler = null;
  }


  private void registerListeners(){
    Bukkit.getPluginManager().registerEvents(new AnvilListener(this),this);
  }

  private void registerCommands(){
  //  getCommand("perms").setExecutor(new CommandListPerms());
    new CommandOpAnvils(this);
  }


  public ConfigManager getConfigManager(){
    return configManager;
  }

  public PlayerHandler getPlayerHandler (){
    return playerHandler;
  }



}
