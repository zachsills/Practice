package net.practice.practice.inventory.inventories;

import lombok.Getter;
import net.practice.practice.Practice;
import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.util.chat.C;
import net.practice.practice.util.itemstack.I;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class EditorInv {

    @Getter private static Inventory inventory = Practice.getInstance().getServer().createInventory(null, 18, C.color("Select a Ladder..."));

    public static void openInventory(Player player) {
         for(Ladder ladder : Ladder.getLadders().values()) {
             if(!ladder.isEditable())
                 continue;

             inventory.addItem(new I(ladder.getDisplayIcon()).clearLore());
         }

         player.openInventory(inventory);
    }
}
