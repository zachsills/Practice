package net.practice.practice.inventory.inventories;

import lombok.Getter;
import net.practice.practice.Practice;
import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.game.queue.Queue;
import net.practice.practice.util.chat.C;
import net.practice.practice.util.itemstack.I;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class RankedInv {

    @Getter
    private static Inventory inventory = Practice.getInstance().getServer().createInventory(null, 18, C.color("&aRanked"));

    public static void updateInventory() {
        if(Ladder.getLadders().values().size() == 0) {
            inventory.setItem(13, new I(Material.RED_ROSE).name(C.color("No ladders set!")));
        } else {
            int index = 0;
            for(Ladder ladder : Ladder.getLadders().values()) {
                if(!ladder.isRanked())
                    continue;

                int queuing = Queue.getNumberQueuing(ladder);
                int inGame = Queue.getNumberInGame(ladder);
                inventory.setItem(index, new I(ladder.getDisplayIcon()).amount(1).clearLore().lore(C.color("&f&m------------")).lore(C.color("&7Queuing: &c" + queuing))
                        .lore(C.color("&7In Game: &c") + inGame).amount(queuing).lore(C.color("&f&m------------")));
                index++;
            }
        }
    }

    public static void openInventory(Player player) {
        player.openInventory(inventory);
    }
}
