package net.practice.practice.inventory.inventories;

import lombok.Getter;
import net.practice.practice.Practice;
import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.game.player.Profile;
import net.practice.practice.game.queue.Queue;
import net.practice.practice.game.queue.QueueType;
import net.practice.practice.util.chat.C;
import net.practice.practice.util.itemstack.I;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;

public class RankedInv {

    @Getter private static Inventory soloRanked = Practice.getInstance().getServer().createInventory(null, 27, C.color("&6Ranked"));
    @Getter private static Inventory partyRanked = Practice.getInstance().getServer().createInventory(null, 27, C.color("&62v2 Ranked"));

    public static void updateInventory() {
        if(Ladder.getLadders().values().size() == 0) {
            soloRanked.setItem(13, new I(Material.RED_ROSE).name(C.color("No ladders set!")));
        } else {
            int index = 10;
            for(Ladder ladder : Ladder.getLadders().values()) {
                if(!ladder.isRanked())
                    continue;
                if((index + 1) % 9 == 0)
                    index += 2;

                int queuing = ladder.getTotalQueuing(QueueType.RANKED);
                int inGame = Queue.getNumberInGame(ladder, true);
                soloRanked.setItem(index, new I(ladder.getDisplayIcon()).amount(1).clearLore().lore(C.color("&f&m------------")).lore(C.color("&7Queuing: &c" + queuing))
                        .lore(C.color("&7In Game: &c") + inGame).lore(C.color("&f&m------------")).flag(ItemFlag.HIDE_POTION_EFFECTS));
                index++;
            }
        }

        if(Ladder.getLadders().values().size() == 0) {
            partyRanked.setItem(13, new I(Material.RED_ROSE).name(C.color("No ladders set!")));
        } else {
            int index = 10;
            for(Ladder ladder : Ladder.getLadders().values()) {
                if(!ladder.isRanked())
                    continue;
                if((index + 1) % 9 == 0)
                    index += 2;

                int queuing = ladder.getTotalQueuing(QueueType.RANKED_TEAM);
                int inGame = Queue.getPartiesInGame(ladder, true);
                partyRanked.setItem(index, new I(ladder.getDisplayIcon()).amount(1).clearLore().lore(C.color("&f&m------------")).lore(C.color("&7Queuing: &c" + queuing))
                        .lore(C.color("&7In Game: &c") + inGame).lore(C.color("&f&m------------")).flag(ItemFlag.HIDE_POTION_EFFECTS));
                index++;
            }
        }
    }

    public static void openInventory(Player player) {
        if (canOpenRanked(player)) {
            player.openInventory(soloRanked);
        } else {
            player.sendMessage(C.color("&cYou must have 10 unranked wins before playing ranked! (" + Profile.getByPlayer(player).getUnrankedWins() + "/10)"));
        }
    }

    public static void openPartyInventory(Player player) {
        if (canOpenRanked(player)) {
            player.openInventory(partyRanked);
        } else {
            player.sendMessage(C.color("&cYou must have 10 unranked wins before playing ranked! (" + Profile.getByPlayer(player).getUnrankedWins() + "/10)"));
        }
    }

    private static boolean canOpenRanked(Player player) {
        return Profile.getByPlayer(player).getUnrankedWins() > 10;
    }
}
