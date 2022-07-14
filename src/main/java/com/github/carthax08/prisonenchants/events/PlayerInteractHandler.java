package com.github.carthax08.prisonenchants.events;

import com.github.carthax08.prisonenchants.util.Util;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;


public class PlayerInteractHandler implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR){
            if(e.getPlayer().getInventory().getItemInMainHand().getType().name().toLowerCase().contains("pickaxe")){
                Util.openEnchantGui(e.getPlayer());
            }
        }
    }
}
