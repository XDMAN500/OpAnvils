package me.varmetek.opanvils.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Set;

public class CommandListPerms implements CommandExecutor
{

  @Override
  public boolean onCommand (CommandSender sender, Command command, String label, String[] args){
    if(sender.isOp()){
      sender.sendMessage(ChatColor.RED +" You are an operator");
    }

    Set<PermissionAttachmentInfo> perms = sender.getEffectivePermissions();
    for(PermissionAttachmentInfo perm : perms){
      sender.sendMessage("   "+perm.getPermission() + "("+perm.getValue()+")");
      if(perm.getAttachment() != null && perm.getAttachment().getPlugin() != null){
        sender.sendMessage("   "+ ChatColor.RED + perm.getAttachment().getPlugin().getName());

      }
    }

    return true;
  }
}
