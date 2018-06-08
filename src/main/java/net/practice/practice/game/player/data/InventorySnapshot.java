package net.practice.practice.game.player.data;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventorySnapshot {

    @Getter private String name;

    @Getter private ItemStack[] contents, armor;

    @Getter private int hunger;
    @Getter private double health;

    @Getter private Inventory inventory;

    public InventorySnapshot(Player player) {
        if(player == null || !player.isOnline())
            return;

        this.name = player.getName();

        this.contents = player.getInventory().getContents().clone();
        this.armor = player.getInventory().getArmorContents().clone();

        this.hunger = player.getFoodLevel();
        this.health = player.getHealth();
    }

    public void loadInventory() {
        inventory = Bukkit.createInventory(null, 54, name + "'s Inventory");

    }

    public void open(Player player) {
        if(inventory == null)
            loadInventory();

        player.openInventory(inventory);
    }
}