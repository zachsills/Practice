package net.practice.practice.inventory.inventories;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class StatsInv {

    public static void openInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, "Statistics");
    }
}
