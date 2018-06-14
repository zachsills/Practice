package net.practice.practice.inventory.inventories;

import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.game.player.Profile;
import net.practice.practice.spawn.SpawnHandler;
import net.practice.practice.util.chat.C;
import net.practice.practice.util.itemstack.I;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class StatsInv {

    public static void openInventory(Player player) {
        openInventory(player, Profile.getByPlayer(player));
    }

    public static void openInventory(Player player, Profile targetP) {
        String title = player.getName().equals(targetP.getName()) ? "Statistics" : "Statistics of " + targetP.getName();
        Inventory inventory = Bukkit.createInventory(player, 45, title);

        int eloSum = Ladder.getLadders().values().stream().mapToInt(targetP::getElo).sum();
        int eloCount = Ladder.getAllLadders().size();
        int globalElo = eloCount > 0 ? (eloSum / eloCount) : 1000;

        inventory.setItem(13, new I(SpawnHandler.getSkull(targetP.getUuid().toString())).name(C.color("&6&lGlobal Stats"))
                .lore(C.color("&f&m--------------"))
                .lore(C.color("&6Ranked:"))
                .lore(C.color("  &eWins &7- &f" + targetP.getRankedWins()))
                .lore(C.color("  &eLosses &7- &f" + targetP.getRankedLosses()))
                .lore(" ")
                .lore(C.color("&6Unranked"))
                .lore(C.color("  &eWins &7- &f" + targetP.getUnrankedWins()))
                .lore(C.color("  &eLosses &7- &f" + targetP.getUnrankedLosses()))
                .lore(" ")
                .lore(C.color("&c&lGlobal ELO: &f" + globalElo))
                .lore(C.color("&f&m--------------")));

        int index = 28;
        for (Ladder ladder : Ladder.getLadders().values()) {
            String name = C.color("&e&l" + C.strip(ladder.getDisplayIcon().getItemMeta().getDisplayName()));
            inventory.setItem(index, new I(ladder.getDisplayIcon()).name(name).clearLore().amount(1).lore(C.color("&f&m------------"))
                    .lore(C.color("&6ELO &7- &f" + targetP.getElo(ladder)))
                    .lore(C.color("&f&m------------")));
            index++;
        }

        player.openInventory(inventory);
    }
}
