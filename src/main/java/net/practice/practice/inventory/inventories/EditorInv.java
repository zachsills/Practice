package net.practice.practice.inventory.inventories;

import net.practice.practice.Practice;
import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.util.chat.C;
import net.practice.practice.util.itemstack.I;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class EditorInv {

    public static void openInventory(Player player) {
        Inventory inventory = Practice.getInstance().getServer().createInventory(null, 18, C.color("Select a Ladder..."));
         for(Ladder ladder : Ladder.getLadders().values()) {
             if(!ladder.isEditable())
                 continue;

             inventory.addItem(new I(ladder.getDisplayIcon()).clearLore());
         }

         player.openInventory(inventory);
    }

    public static void openSavingInventory(Player player) {

    }

    public static void openLadderItems(Player player, Ladder ladder) {
        Inventory inventory = Bukkit.createInventory(null, 54, "Editing " + ladder.getName());
        inventory.setContents(ladder.getEditor().getContents());

        player.openInventory(inventory);
    }
}
