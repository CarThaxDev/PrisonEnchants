package com.github.carthax08.prisonenchants.util;

import com.github.carthax08.prisonenchants.PluginMain;
import com.github.carthax08.prisonenchants.util.enums.CustomEnchant;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class Util {
    public static final Vector<Player> debounce = new Vector<>();
    public static final HashMap<Integer, String> crateCommandMap = new HashMap<>();

    public static void startDebounce(Player player, long ticks){
        debounce.add(player);
        new BukkitRunnable(){
            @Override
            public void run(){
                debounce.remove(player);
            }
        }.runTaskLaterAsynchronously(PluginMain.getInstance(), ticks);
    }
    public static void openEnchantGui(Player player) {
        if(!player.getInventory().getItemInMainHand().getType().name().toLowerCase().contains("pickaxe")){
            return;
        }
        Inventory inventory = Bukkit.createInventory(null, 27, "Enchants");
        //TODO: Custom Enchants T-T
        for(CustomEnchant enchant : CustomEnchant.values()){
            ItemStack item = new ItemStack(enchant.displayMaterial);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', enchant.displayName));
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.translateAlternateColorCodes('&', enchant.description));
            ItemMeta mainHandMeta = player.getInventory().getItemInMainHand().getItemMeta();
            int currentLevel;
            NamespacedKey key = new NamespacedKey(PluginMain.getInstance(), enchant.name().toLowerCase() + ".level");
            if(enchant == CustomEnchant.FORTUNE){
                currentLevel = mainHandMeta.getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS);
            }else{
                PersistentDataContainer container = mainHandMeta.getPersistentDataContainer();
                currentLevel = container.has(key, PersistentDataType.INTEGER) ? container.get(key, PersistentDataType.INTEGER) : 0;
            }
            lore.add(ChatColor.GREEN + "Level: " + currentLevel + "/" + enchant.maxLevel);
            lore.add(ChatColor.GREEN + "Cost: " + ChatColor.GOLD + enchant.price);
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(PluginMain.costKey, PersistentDataType.INTEGER, enchant.price);
            container.set(PluginMain.typeKey, PersistentDataType.STRING, enchant.name());
            meta.setLore(lore);
            item.setItemMeta(meta);
            inventory.setItem(enchant.slot, item);

        }
        player.openInventory(inventory);
    }
    public static void loadKeyChances(FileConfiguration config){
        ConfigurationSection sec = config.getConfigurationSection("keys");
        for(String key : sec.getKeys(false)){
            int chance = (int) Math.floor(1000 * (sec.getDouble(key + ".chance")/100));
            crateCommandMap.put(chance, sec.getString(key + ".command"));
        }
    }
}
