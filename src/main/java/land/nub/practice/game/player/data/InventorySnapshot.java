package net.practice.practice.game.player.data;

import lombok.Getter;
import net.practice.practice.game.player.Profile;
import net.practice.practice.util.chat.C;
import net.practice.practice.util.itemstack.I;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;

public class InventorySnapshot {

    @Getter private String name;

    @Getter private ItemStack[] contents, armor;

    @Getter private Player player;

    @Getter private int hunger;
    @Getter private double health;

    @Getter private int missedPots, longestCombo;
    @Getter private double accuracy;

    @Getter private Inventory inventory;

    public InventorySnapshot(Player player) {
        if(player == null || !player.isOnline())
            return;

        this.player = player;
        this.name = player.getName();

        this.contents = player.getInventory().getContents().clone();
        this.armor = player.getInventory().getArmorContents().clone();

        this.hunger = player.getFoodLevel();
        this.health = player.getHealth();

        if(Profile.getByPlayer(player).getCurrentDuel() == null)
            return;

        this.longestCombo = Profile.getByPlayer(player).getCurrentDuel().getLongestCombo(player);

        if(Profile.getByPlayer(player).getCurrentDuel().getMissedPots().containsKey(player))
            this.missedPots = Profile.getByPlayer(player).getCurrentDuel().getMissedPots().get(player);

        if(Profile.getByPlayer(player).getCurrentDuel().getThrownPots().containsKey(player)) {
            int thrownPots = Profile.getByPlayer(player).getCurrentDuel().getThrownPots().get(player);
            //this.accuracy = (((double) initialPots - this.missedPots) / (double) initialPots) * 100.0;
            this.accuracy = ((thrownPots - missedPots) / (double) thrownPots) * 100.0;
        }
    }

    public void loadInventory() { // Detect soups when it's a soup match
        inventory = Bukkit.createInventory(null, 54, name + "'s Inventory");

        for(int i = 0; i < contents.length; i++)
            inventory.setItem(i, contents[i]);

        int i = 36;
        for(ItemStack item : armor) {
            inventory.setItem(i, item);
            i++;
        }

        inventory.setItem(47, new I(Material.SPECKLED_MELON).name(C.color("&ePlayer Info")).clearLore().lore(C.color("&dHearts: &7" + (Math.round(health * 2) / 2.0))));

        int potsRemaining = (int) Arrays.stream(contents)
                .filter(Objects::nonNull)
                .filter(itemStack -> itemStack.getType() == Material.POTION && itemStack.getDurability() == 16421)
                .count();
        inventory.setItem(49, new I(Material.POTION).durability(16461).amount(potsRemaining).name(C.color("&6Potion Info")).lore(C.color("&dMissed: &e" + missedPots)).lore(C.color("&dPots Remaining: &e" + potsRemaining)).lore(C.color("&dPotion Accuracy: &e" + String.format("%.1f", accuracy) + "%")));

        inventory.setItem(51, new I(Material.DIAMOND_SWORD).name(C.color("&eMatch Info")).lore(C.color("&bLongest Combo: " + longestCombo)));
    }

    public void open(Player player) {
        if(inventory == null)
            loadInventory();

        player.openInventory(inventory);
    }
}