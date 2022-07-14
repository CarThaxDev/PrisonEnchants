package com.github.carthax08.prisonenchants.commands;

import com.github.carthax08.prisonenchants.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnchantsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Util.openEnchantGui((Player) sender);
        }else{
            sender.sendMessage(ChatColor.RED + "You can't open that GUI because you are not a player!");
        }
        return true;
    }
}
