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
import org.bukkit.inventory.ItemFlag;

public class UnrankedInv {

    @Getter private static Inventory soloUnranked = Practice.getInstance().getServer().createInventory(null, 27, C.color("&eUnranked"));
    @Getter private static Inventory partyUnranked = Practice.getInstance().getServer().createInventory(null, 27, C.color("&e2v2 Unranked"));


    public static void updateInventory() {
        if(Ladder.getLadders().values().size() == 0) {
            soloUnranked.setItem(13, new I(Material.RED_ROSE).name(C.color("No ladders set!")));
        } else {
            int index = 10;
            for(Ladder ladder : Ladder.getLadders().values()) {
                if((index + 1) % 9 == 0)
                    index += 2;

                int queuing = ladder.getTotalQueuing(QueueType.UNRANKED);
                int inGame = Queue.getNumberInGame(ladder, false);
                soloUnranked.setItem(index, new I(ladder.getDisplayIcon()).clearLore().amount(1).lore(C.color("&f&m------------")).lore(C.color("&7Queuing: &c" + queuing))
                        .lore(C.color("&7In Game: &c") + inGame).lore(C.color("&f&m------------")).flag(ItemFlag.HIDE_POTION_EFFECTS));

                index++;
            }
        }

        if(Ladder.getLadders().values().size() == 0) {
            partyUnranked.setItem(13, new I(Material.RED_ROSE).name(C.color("No ladders set!")));
        } else {
            int index = 10;
            for(Ladder ladder : Ladder.getLadders().values()) {
                if((index + 1) % 9 == 0)
                    index += 2;

                int queuing = ladder.getTotalQueuing(QueueType.UNRANKED);
                int inGame = Queue.getPartiesInGame(ladder, false);
                partyUnranked.setItem(index, new I(ladder.getDisplayIcon()).clearLore().amount(1).lore(C.color("&f&m------------")).lore(C.color("&7Queuing: &c" + queuing))
                        .lore(C.color("&7In Game: &c") + inGame).lore(C.color("&f&m------------")).flag(ItemFlag.HIDE_POTION_EFFECTS));

                index++;
            }
        }
    }

    public static void openInventory(Player player) {
        player.openInventory(soloUnranked);
    }

    public static void openPartyInventory(Player player) {
        player.openInventory(partyUnranked);
    }
}