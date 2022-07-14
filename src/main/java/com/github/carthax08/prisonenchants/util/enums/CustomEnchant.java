package com.github.carthax08.prisonenchants.util.enums;

import org.bukkit.Material;

public enum CustomEnchant {
    FORTUNE(2, 5000, 10000, "Fortune", "Increase the number of blocks you get from mining!", Material.DIAMOND),
    TOKENATOR(4, 1500, 250, "Coin Finder", "Increase your chances of finding NovaCoins while mining!", Material.SUNFLOWER),
    KEY_FINDER(13, 10000, 50, "Key Finder", "Have a chance to find Keys while mining!", Material.TRIPWIRE_HOOK),
    EXPLOSION(20, 20000, 5000, "Explosion", "Have a chance to cause an explosion while mining", Material.TNT),
    JACKHAMMER(6, 20000, 10000, "Jackhammer", "Have a chance to clear an entire layer of the mine!",Material.ANVIL),
    MULTI_DIRECTIONAL(24, 10000, 5000, "Multi-Directional", "Have a chance to mine a cross (+) in the mine!", Material.RED_STAINED_GLASS),
    LASER(15, 10000, 10000, "Laser", "Have a chance to mine a straight line in the direction you're facing!", Material.RED_STAINED_GLASS_PANE),
    HASTE(11, 20000, 3, "Haste", "Give yourself the Vanilla Haste status effect while holding!", Material.GOLDEN_PICKAXE),
    METAL_DETECTOR(22, 10000, 100, "Metal Detector", "Chance to get a random money boost for a random amount of time when you break a block!", Material.IRON_ORE);


    public final int slot, price, maxLevel;
    public final String displayName, description;
    public final Material displayMaterial;

    CustomEnchant(int slot, int price, int maxLevel, String displayName, String description, Material displayMaterial){
        this.slot = slot;
        this.price = price;
        this.maxLevel = maxLevel;
        this.displayName = displayName;
        this.displayMaterial = displayMaterial;
        this.description = description;
    }
}
