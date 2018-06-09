package net.practice.practice.inventory.inventories;

import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.util.itemstack.I;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class RequestInv {

    public static void openInventory(Player requester, Player requested) {
        Inventory inventory = Bukkit.createInventory(requester, 18, "Requesting: " + requested.getName());

        for(Ladder ladder : Ladder.getLadders().values())
            inventory.addItem(new I(ladder.getDisplayIcon()).amount(1).name(ladder.getDisplayName()));

        requester.openInventory(inventory);
    }
}
