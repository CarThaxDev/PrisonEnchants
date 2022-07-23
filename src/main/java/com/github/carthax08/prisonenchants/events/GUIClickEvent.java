package com.github.carthax08.prisonenchants.events;

import com.github.carthax08.prisonenchants.PluginMain;
import com.github.carthax08.prisonenchants.util.Util;
import com.github.carthax08.prisonenchants.util.enums.CustomEnchant;
import com.github.carthax08.servercore.api.Players;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

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
            PersistentDataContainer container = event.getCurrentItem().getItemMeta().getPersistentDataContainer();
            String enchantName = container.get(PluginMain.typeKey, PersistentDataType.STRING);
            int cost = container.get(PluginMain.costKey, PersistentDataType.INTEGER);
            if(cost == 0){
                System.out.println("An error occurred while a player tried to enchant their item! There was no cost set for the enchant!");
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
                NamespacedKey key = new NamespacedKey(PluginMain.getInstance(), enchantName.toLowerCase() + ".level");
                if(enchantName.equalsIgnoreCase("Fortune")){
                    level = meta.getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS) + 1;
                    meta.removeEnchant(Enchantment.LOOT_BONUS_BLOCKS);
                    if(level > CustomEnchant.valueOf(enchantName.toUpperCase().replace(" ", "_")).maxLevel){
                        player.sendMessage(ChatColor.RED + "That is already at the max level!");
                        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 5f, 5f);
                        return;
                    }
                    meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, level, true);
                }else{
                    level = container.has(key, PersistentDataType.INTEGER) ? container.get(key, PersistentDataType.INTEGER) : 0;
                    if(level > CustomEnchant.valueOf(enchantName.toUpperCase().replace(" ", "_")).maxLevel){
                        player.sendMessage(ChatColor.RED + "That is already at the max level!");
                        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 5f, 5f);
                        return;
                    }
                    container.set(key, PersistentDataType.INTEGER, level + 1);
                }
                List<String> lore;
                if(meta.hasLore()){
                    lore = meta.getLore();
                }else{
                    lore = new ArrayList<>();
                }

                if(level == 0) level = 1;
                lore.add(ChatColor.RESET + "" + ChatColor.GRAY + enchantName + " " + level);
                meta.setLore(lore);
                item.setItemMeta(meta);
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 5f, 5f);
                Util.startDebounce(player, 5);
                Players.setTokens(player, tokens - cost);
                Util.openEnchantGui(((Player) event.getWhoClicked()).getPlayer());
            }
        }
    }
}
