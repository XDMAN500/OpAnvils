package me.varmetek.opanvils.utility;

import com.google.common.base.Preconditions;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collection;

public final class TextUtil
{

  public static String  color(String text){
    return ChatColor.translateAlternateColorCodes('&',text);
  }


  public static void sendMessage(CommandSender sender, String... messages){
    Preconditions.checkNotNull(sender,"Sender cannot be null");
    Preconditions.checkNotNull(messages,"Cannot send null messages");

    if(messages.length == 0){
      sender.sendMessage(" ");
    }else{
      for(String msg: messages){
        if(msg == null) continue;
        sender.sendMessage(color(msg));
      }
    }
  }

  public static void sendMessage(CommandSender[] senders, String... messages){
    Preconditions.checkNotNull(senders,"Senders cannot be null");
    Preconditions.checkArgument(senders.length != 0,"Senders cannot be empty");
    Preconditions.checkNotNull(messages,"Cannot send null messages");

    if(messages.length == 0){
      for(CommandSender s: senders){
        s.sendMessage(" ");
      }

    }else{
      for(String msg: messages){
        if(msg == null) continue;
        for(CommandSender s: senders){
          s.sendMessage(color(msg));
        }
      }
    }
  }


  public static void sendMessage(Collection<CommandSender> senders, String... messages){
    Preconditions.checkNotNull(senders,"Senders cannot be null");
    Preconditions.checkArgument(!senders.isEmpty(),"Senders cannot be empty");
    Preconditions.checkNotNull(messages,"Cannot send null messages");

    if(messages.length == 0){
      for(CommandSender s: senders){
        s.sendMessage(" ");
      }

    }else{
      for(String msg: messages){
        if(msg == null) continue;
        for(CommandSender s: senders){
          s.sendMessage(color(msg));
        }
      }
    }
  }

  public static void sendMessage(CommandSender sender, Collection<String> messages){
    Preconditions.checkNotNull(sender,"Sender cannot be null");
    Preconditions.checkNotNull(messages,"Cannot send null messages");

    if(messages.size() == 0){
      sender.sendMessage(" ");
    }else{
      for(String msg: messages){
        if(msg == null) continue;
        sender.sendMessage(color(msg));
      }
    }
  }

  public static void sendMessage(CommandSender[] senders, Collection<String> messages){
    Preconditions.checkNotNull(senders,"Senders cannot be null");
    Preconditions.checkArgument(senders.length != 0,"Senders cannot be empty");
    Preconditions.checkNotNull(messages,"Cannot send null messages");

    if(messages.size() == 0){
      for(CommandSender s: senders){
        s.sendMessage(" ");
      }

    }else{
      for(String msg: messages){
        if(msg == null) continue;
        for(CommandSender s: senders){
          s.sendMessage(color(msg));
        }
      }
    }
  }


  public static void sendMessage(Collection<CommandSender> senders, Collection<String> messages){
    Preconditions.checkNotNull(senders,"Senders cannot be null");
    Preconditions.checkArgument(!senders.isEmpty(),"Senders cannot be empty");
    Preconditions.checkNotNull(messages,"Cannot send null messages");

    if(messages.size() == 0){
      for(CommandSender s: senders){
        s.sendMessage(" ");
      }

    }else{
      for(String msg: messages){
        if(msg == null) continue;
        for(CommandSender s: senders){
          s.sendMessage(color(msg));
        }
      }
    }
  }
}
