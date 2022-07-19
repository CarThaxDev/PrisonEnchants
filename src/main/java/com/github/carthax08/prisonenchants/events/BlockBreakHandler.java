package com.github.carthax08.prisonenchants.events;

import com.github.carthax08.prisonenchants.PluginMain;
import com.github.carthax08.prisonenchants.util.Util;
import com.github.carthax08.prisonenchants.util.enums.CustomEnchant;
import com.github.carthax08.servercore.data.ServerPlayer;
import com.github.carthax08.servercore.data.files.BlocksFileHandler;
import com.github.carthax08.servercore.util.DataStore;
import com.sk89q.worldedit.blocks.Blocks;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockBreakHandler implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(e.getPlayer());
        com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(e.getBlock().getLocation());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        if(!query.testState(loc, localPlayer, Flags.BLOCK_BREAK)){
            return;
        }
        ItemMeta meta = e.getPlayer().getInventory().getItemInMainHand().getItemMeta();
        List<String> lore = meta.getLore();
        List<Block> tokenBlocks = new ArrayList<>();
        int tokenatorLevel = 0;
        if(meta.hasLore()) {
            for (String line : lore) {
                String stripped = ChatColor.stripColor(line);
                if (stripped.toLowerCase().contains("explosion")) {
                    int currentLevel = Integer.parseInt(stripped.replace("Explosion ", ""));
                    boolean proc = new Random().nextInt(1, 1000) > (1000 - (Math.max((250 * (currentLevel / CustomEnchant.EXPLOSION.maxLevel)), 1)));
                    if (proc) {
                        List<Block> blocks = getNearbyBlocks(e.getBlock().getLocation(), (int) (1 + (1 * (Math.floor(currentLevel / 1000)))), 1, e.getPlayer());
                        List<ItemStack> items = new ArrayList<>();
                        for (Block block : blocks) {
                            tokenBlocks.add(block);
                            items.addAll(block.getDrops());
                            block.setType(Material.AIR);
                        }
                        for (ItemStack item : items) {
                            item.setAmount(item.getAmount() * meta.getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS));
                            DataStore.getPlayerData(e.getPlayer()).addItemToBackpack(item);
                        }
                    }
                }
                if (stripped.toLowerCase().contains("metal detector")) {
                    Random random = new Random();
                    int currentLevel = Integer.parseInt(stripped.toLowerCase().replace("metal detector ", ""));
                    boolean proc = random.nextInt(1, 1000) > 995;
                    if (proc) {
                        long length = (long) (20 * (60 * (Math.floor(currentLevel / 10.0) + 1)));
                        float boost = random.nextInt(1, 10 * (1 + currentLevel / 10));
                        e.getPlayer().sendMessage(ChatColor.GREEN + "Metal Detector just gave you a +" + boost / 10 + " sell multiplier for " + (length / 20 / 60) + " minutes!");
                        DataStore.getPlayerData(e.getPlayer()).sellMultiplier += boost / 10.0;
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                DataStore.getPlayerData(e.getPlayer()).sellMultiplier -= boost / 10.0;
                                e.getPlayer().sendMessage(ChatColor.RED + "Your +" + boost / 10.0 + " sell multiplier has worn off!");
                            }
                        }.runTaskLaterAsynchronously(PluginMain.getInstance(), length);
                    }
                }
                if (stripped.toLowerCase().contains("tokenator")) {
                    tokenatorLevel = Integer.parseInt(stripped.toLowerCase().replace("tokenator ", ""));
                }
                if(stripped.toLowerCase().contains("laser")) {
                    System.out.println("Laser found");
                    int currentLevel = Integer.parseInt(stripped.toLowerCase().replace("laser ", ""));
                    System.out.println(currentLevel);
                    boolean proc = new Random().nextInt(1, 1000) > (1000 - Math.max(1, (100 * currentLevel/CustomEnchant.LASER.maxLevel)));
                    if (proc) {
                        double yaw = e.getPlayer().getLocation().getYaw();
                        List<Block> blocks = new ArrayList<>();
                        blocks.add(e.getBlock());
                        Location locPlus = new Location(e.getBlock().getWorld(), e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ());
                        Location locMinus = new Location(e.getBlock().getWorld(), e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ());
                        if ((yaw <= 45) || (yaw > 135 && yaw <= 225) || (yaw > 315) ) {
                            while (true) {
                                locPlus.setZ(locPlus.getZ()+1);
                                Block newBlock = locPlus.getBlock();
                                if (newBlock.getType() == Material.BEDROCK) {
                                    break;
                                }
                                blocks.add(newBlock);
                            }
                            while (true) {
                                locMinus.setZ(locMinus.getZ()-1);
                                Block newBlock = locMinus.getBlock();
                                if (newBlock.getType() == Material.BEDROCK) {
                                    break;
                                }
                                blocks.add(newBlock);
                            }
                        } else if ((yaw > 45 && yaw <= 135 ) || (yaw > 225 && yaw <= 315)) {
                            while (true) {
                                locPlus.setX(locPlus.getX()+1);
                                Block newBlock = locPlus.getBlock();
                                if (newBlock.getType() == Material.BEDROCK) {
                                    break;
                                }
                                blocks.add(newBlock);
                            }
                            while (true) {
                                locMinus.setX(locMinus.getX()-1);
                                Block newBlock = locMinus.getBlock();
                                if (newBlock.getType() == Material.BEDROCK) {
                                    break;
                                }
                                blocks.add(newBlock);
                            }
                        }
                        for (Block b : blocks) {
                            Location yPlus = new Location(b.getWorld(), b.getX(), b.getY(), b.getZ());
                            Location yMinus = new Location(b.getWorld(), b.getX(), b.getY(), b.getZ());
                            while (true) {
                                yPlus.setY(locPlus.getY()+1);
                                Block newBlock = locPlus.getBlock();
                                if (newBlock.getType() == Material.BEDROCK) {
                                    break;
                                }
                                blocks.add(newBlock);
                            }
                            while (true) {
                                yMinus.setY(locMinus.getY()-1);
                                Block newBlock = yMinus.getBlock();
                                if (newBlock.getType() == Material.BEDROCK) {
                                    break;
                                }
                                blocks.add(newBlock);
                            }
                        }
                        List<ItemStack> items = new ArrayList<>();
                        for (Block b : blocks) {
                            tokenBlocks.add(b);
                            items.addAll(b.getDrops());
                            b.setType(Material.AIR);
                        }
                        for (ItemStack item : items) {
                            item.setAmount(item.getAmount() * meta.getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS));
                            DataStore.getPlayerData(e.getPlayer()).addItemToBackpack(item);
                        }
                    }
                }
                if(stripped.toLowerCase().contains("multidirectional")) {
                    System.out.println("multidirectional found");
                    int currentLevel = Integer.parseInt(stripped.toLowerCase().replace("multidirectional ", ""));
                    System.out.println(currentLevel);
                    boolean proc = new Random().nextInt(1, 1000) > (1000 - Math.max(1, (100 * currentLevel/CustomEnchant.LASER.maxLevel)));
                    if (proc) {
                        double yaw = e.getPlayer().getLocation().getYaw();
                        List<Block> blocks = new ArrayList<>();
                        blocks.add(e.getBlock());
                        Location locPlus = new Location(e.getBlock().getWorld(), e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ());
                        Location locMinus = new Location(e.getBlock().getWorld(), e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ());
                        while (true) {
                            locPlus.setZ(locPlus.getZ()+1);
                            Block newBlock = locPlus.getBlock();
                            if (newBlock.getType() == Material.BEDROCK) {
                                break;
                            }
                            blocks.add(newBlock);
                        }
                        while (true) {
                            locMinus.setZ(locMinus.getZ()-1);
                            Block newBlock = locMinus.getBlock();
                            if (newBlock.getType() == Material.BEDROCK) {
                                break;
                            }
                            blocks.add(newBlock);
                        }
                        while (true) {
                            locPlus.setX(locPlus.getX()+1);
                            Block newBlock = locPlus.getBlock();
                            if (newBlock.getType() == Material.BEDROCK) {
                                break;
                            }
                            blocks.add(newBlock);
                        }
                        while (true) {
                            locMinus.setX(locMinus.getX()-1);
                            Block newBlock = locMinus.getBlock();
                            if (newBlock.getType() == Material.BEDROCK) {
                                break;
                            }
                            blocks.add(newBlock);
                        }
                        for (Block b : blocks) {
                            Location yPlus = new Location(b.getWorld(), b.getX(), b.getY(), b.getZ());
                            Location yMinus = new Location(b.getWorld(), b.getX(), b.getY(), b.getZ());
                            while (true) {
                                yPlus.setY(locPlus.getY()+1);
                                Block newBlock = locPlus.getBlock();
                                if (newBlock.getType() == Material.BEDROCK) {
                                    break;
                                }
                                blocks.add(newBlock);
                            }
                            while (true) {
                                yMinus.setY(locMinus.getY()-1);
                                Block newBlock = yMinus.getBlock();
                                if (newBlock.getType() == Material.BEDROCK) {
                                    break;
                                }
                                blocks.add(newBlock);
                            }
                        }
                        List<ItemStack> items = new ArrayList<>();
                        for (Block b : blocks) {
                            tokenBlocks.add(b);
                            items.addAll(b.getDrops());
                            b.setType(Material.AIR);
                        }
                        for (ItemStack item : items) {
                            item.setAmount(item.getAmount() * meta.getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS));
                            DataStore.getPlayerData(e.getPlayer()).addItemToBackpack(item);
                        }
                    }
                }
                if(stripped.toLowerCase().contains("jackhammer")){
                    System.out.println("Jackhammer found");
                    int currentLevel = Integer.parseInt(stripped.toLowerCase().replace("jackhammer ", ""));
                    System.out.println(currentLevel);
                    boolean proc = new Random().nextInt(1, 1000) > (1000 - Math.max(1, (100 * Math.floor(currentLevel/CustomEnchant.JACKHAMMER.maxLevel))));
                    System.out.println(proc);
                    if(proc){
                        System.out.println("Procced");
                        List<Block> blocks = getNearbyBlocks(e.getBlock().getLocation(), 100, 0, e.getPlayer());
                        List<ItemStack> items = new ArrayList<>();
                        for (Block block : blocks) {
                            tokenBlocks.add(block);
                            items.addAll(block.getDrops());
                            block.setType(Material.AIR);
                        }
                        for (ItemStack item : items) {
                            item.setAmount(item.getAmount() * meta.getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS));
                            DataStore.getPlayerData(e.getPlayer()).addItemToBackpack(item);
                        }
                    }
                }
                if(stripped.toLowerCase().contains("key finder")){
                    Random random = new Random();
                    int currentLevel = Integer.parseInt(stripped.toLowerCase().replace("key finder", ""));
                    boolean proc = random.nextInt(1, 1000) > (1000 - (Math.max(1, (100 * Math.floor(currentLevel/CustomEnchant.KEY_FINDER.maxLevel)))));
                    if(proc){
                        int key = random.nextInt(1, 1000);
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), Util.crateCommandMap.get(key));
                    }
                }
            }
        }
        tokenBlocks.add(e.getBlock());
        for(Block block : tokenBlocks){
            handleTokens(e.getPlayer(), tokenatorLevel, block);
        }
    }






    public static List<Block> getNearbyBlocks(Location location, int radius, int height, Player player) {
        System.out.println("Explosion/jackhammer proced, checking for nearby blocks.");
        List<Block> blocks = new ArrayList<>();
        for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for(int y = location.getBlockY() - height; y <= location.getBlockY() + height; y++) {
                for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    Block block = location.getWorld().getBlockAt(x, y, z);
                    LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
                    com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(block.getLocation());
                    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                    RegionQuery query = container.createQuery();


                    if (query.testState(loc, localPlayer, Flags.BLOCK_BREAK)) {
                        blocks.add(block);
                    }
                }
            }
        }
        return blocks;
    }
    public void handleTokens(Player player, int tokenatorLevel, Block block){
        if(BlocksFileHandler.blocksConfig.getStringList("blocks").contains(block.getType().name().toLowerCase())) {
            ServerPlayer playerData = DataStore.getPlayerData(player);
            Random random = new Random();
            if (random.nextInt(1, 1000) > 1000 - (10 + (240 * tokenatorLevel / CustomEnchant.TOKENATOR.maxLevel))) {
                System.out.println("Coins found!");
                int tokens = random.nextInt(500, 1000);
                playerData.tokenBalance += tokens;
                playerData.savePlayerData(false);
                player.sendMessage(ChatColor.YELLOW + "You randomly found " + tokens + " NovaCoins!");
            }
        }
    }
    @EventHandler
    public void playerSwitchItem(PlayerItemHeldEvent e){
        ItemStack item = e.getPlayer().getInventory().getItem(e.getNewSlot());
        boolean hasted = false;
        if(item != null && item.hasItemMeta()){
            ItemMeta meta = item.getItemMeta();
            if(meta.hasLore()){
                List<String> lore = meta.getLore();
                for (String line : lore){
                    String stripped = ChatColor.stripColor(line);
                    if(stripped.toLowerCase().contains("haste")){
                        int level = Integer.parseInt(stripped.toLowerCase().replace("haste ", ""));
                        e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, level-1, false, false, false));
                        hasted = true;
                    }
                }
            }
        }
        if(!hasted){
            e.getPlayer().removePotionEffect(PotionEffectType.FAST_DIGGING);
        }
    }
}
