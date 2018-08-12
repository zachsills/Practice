package net.practice.practice.inventory.inventories;

import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.util.chat.C;
import net.practice.practice.util.itemstack.I;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;

public class RequestInv {

    public static void openInventory(Player requester, Player requested) {
        Inventory inventory = Bukkit.createInventory(requester, 27, C.color("&eRequesting: " + requested.getName()));

        int index = 10;
        for(Ladder ladder : Ladder.getLadders().values()) {
            if((index + 1) % 9 == 0)
                index += 2;

            inventory.setItem(index, new I(ladder.getDisplayIcon()).amount(1).name(ladder.getDisplayName()).clearLore().flag(ItemFlag.HIDE_POTION_EFFECTS));
            index++;
        }

        requester.openInventory(inventory);
    }
}
