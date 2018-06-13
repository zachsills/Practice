package net.practice.practice.inventory.inventories;

import lombok.Getter;
import net.practice.practice.Practice;
import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.game.queue.Queue;
import net.practice.practice.game.queue.QueueType;
import net.practice.practice.util.chat.C;
import net.practice.practice.util.itemstack.I;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class UnrankedInv {

    @Getter private static Inventory inventory = Practice.getInstance().getServer().createInventory(null, 18, C.color("&eUnranked"));

    public static void updateInventory() {
        if(Ladder.getLadders().values().size() == 0) {
            inventory.setItem(13, new I(Material.RED_ROSE).name(C.color("No ladders set!")));
        } else {
            int index = 0;
            for(Ladder ladder : Ladder.getLadders().values()) {
                int queuing = ladder.getTotalQueuing(QueueType.UNRANKED);
                int inGame = Queue.getNumberInGame(ladder, false);
                inventory.setItem(index, new I(ladder.getDisplayIcon()).clearLore().amount(1).lore(C.color("&f&m------------")).lore(C.color("&7Queuing: &c" + queuing))
                        .lore(C.color("&7In Game: &c") + inGame).lore(C.color("&f&m------------")));
                index++;
            }
        }
    }

    public static void openInventory(Player player) {
        player.openInventory(inventory);
    }
}