package net.practice.practice.inventory;

import net.practice.practice.Practice;
import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.game.player.Profile;
import net.practice.practice.util.chat.C;
import net.practice.practice.util.itemstack.I;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class UnrankedInv {

    public static void openInventory(Player player) {
        Inventory inventory = Practice.getInstance().getServer().createInventory(player, 27, C.color("&eUnranked"));

        Profile profile = Profile.getByPlayer(player);

        if (Ladder.getLadders().values().size() == 0) {
            inventory.setItem(13, new I(Material.RED_ROSE).name(C.color("No ladders set!")));
        } else {
            int index = 10;
            for (Ladder ladder : Ladder.getLadders().values()) {
                int queuing = profile.getNumberQueuing(ladder);
                inventory.setItem(index, new I(ladder.getDisplayIcon()).clearLore().lore(C.color("&7Queuing: " + queuing)).lore(C.color("&7In-Game: xd")).amount(queuing));
                index++;
            }
        }

        player.openInventory(inventory);
    }
}
