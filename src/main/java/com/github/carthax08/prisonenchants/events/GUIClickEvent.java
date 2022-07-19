package com.github.carthax08.prisonenchants.events;

import com.github.carthax08.prisonenchants.util.Util;
import com.github.carthax08.prisonenchants.util.enums.CustomEnchant;
import com.github.carthax08.servercore.api.Players;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GUIClickEvent implements Listener {
    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent event){
        if(event.getView().getTitle().equalsIgnoreCase("Enchants")){
            event.setCancelled(true);
            if(Util.debounce.contains((Player) event.getWhoClicked())){
                return;
            }
            Player player = (Player) event.getWhoClicked();
            double tokens = Players.getTokens(player);
            double cost = 0;
            String enchantName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
            System.out.println(enchantName);
            for (String string : event.getCurrentItem().getItemMeta().getLore()){
                string = ChatColor.stripColor(string);
                if(string.contains("Cost:")){
                    string = string.replace("Cost: ", "");
                    cost = Double.parseDouble(string);
                }
            }
            if(cost == 0){
                System.out.println("An error occured while a player tried to enchant their item! There was no cost set for the enchant!");
                return;
            }
            if(tokens <= cost){
                player.sendMessage(ChatColor.RED + "You don't have enough tokens for that!");
            }else{
                ItemStack item = player.getInventory().getItemInMainHand();
                ItemMeta meta = item.getItemMeta();
                if(!meta.getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    List<String> lore = new ArrayList<>();
                    for(Enchantment enchant : meta.getEnchants().keySet()){
                        String name = enchant.getKey().toString().toLowerCase();
                        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + name.replace(name.charAt(0), Character.toUpperCase(name.charAt(0))) + " " + meta.getEnchants().get(enchant));
                    }
                    meta.setLore(lore);
                }
                int level = 0;
                if(enchantName.equalsIgnoreCase("Fortune")){
                    System.out.println("1");
                    level = meta.getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS) + 1;
                    System.out.println(level);
                    meta.removeEnchant(Enchantment.LOOT_BONUS_BLOCKS);
                    meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, level, true);
                }
                List<String> lore;
                if(meta.hasLore()){
                    lore = meta.getLore();
                }else{
                    lore = new ArrayList<>();
                }
                List<String> iteratorList = new ArrayList<>(lore);
                for(String string : iteratorList){
                    String oldString = string;
                    string = ChatColor.stripColor(string);
                    if(string.contains(enchantName)){
                        if (level == 0) {
                            level = Integer.parseInt(string.replace(enchantName + " ", "")) + 1;
                        }
                        lore.remove(oldString);
                    }
                }
                if(level == 0) level = 1;
                if(level > CustomEnchant.valueOf(enchantName.toUpperCase().replace(" ", "_")).maxLevel){
                    player.sendMessage(ChatColor.RED + "That is already at the max level!");
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 5f, 5f);
                    return;
                }
                lore.add(ChatColor.RESET + "" + ChatColor.GRAY + enchantName + " " + level);
                meta.setLore(lore);
                item.setItemMeta(meta);
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 5f, 5f);
                Util.startDebounce(player, 5);
                Players.setTokens(player, tokens - cost);
                /*
                Just reloads GUI because I can't be bothered to figure out how to make prices increment
                with the way it goes rn. I mean seriously, it charges based off LORE?? ewwwww
                */
                Util.openEnchantGui(((Player) event.getWhoClicked()).getPlayer());
            }
        }
    }
}
