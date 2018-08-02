package net.practice.practice.inventory.inventories;

import lombok.Getter;
import lombok.Setter;
import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.game.player.Profile;
import net.practice.practice.spawn.SpawnHandler;
import net.practice.practice.util.chat.C;
import net.practice.practice.util.itemstack.I;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatsInv {

    @Getter @Setter private static Map<Ladder, Map<String, Integer>> top = new HashMap<>();

    public static void openInventory(Player player) {
        openInventory(player, Profile.getByPlayer(player));
    }

    public static void openInventory(Player player, Profile targetP) {
        String title = player.getName().equals(targetP.getName()) ? C.color("&cStats") : C.color("&cStats of &6" + targetP.getName());
        Inventory inventory = Bukkit.createInventory(player, 45, title);

        int eloSum = Ladder.getLadders().values().stream().mapToInt(targetP::getElo).sum();
        int eloCount = Ladder.getAllLadders().size();
        int globalElo = eloCount > 0 ? (eloSum / eloCount) : 1000;

        inventory.setItem(13, new I(SpawnHandler.getSkull(targetP.getUuid().toString())).name(C.color("&e&l" + targetP.getName()))
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
        for(Ladder ladder : Ladder.getLadders().values()) {
            String name = C.color("&e&l" + C.strip(ladder.getDisplayIcon().getItemMeta().getDisplayName()));

            String whosElo = (player.getUniqueId().equals(targetP.getUuid())) ? "Your" : (targetP.getName() + "'s");

            I item = new I(ladder.getDisplayIcon()).name(name).clearLore().amount(1).flag(ItemFlag.HIDE_POTION_EFFECTS)
                    .lore(C.color("&f&m------------"))
                    .lore(C.color("&6" + whosElo + " ELO &7- &f" + targetP.getElo(ladder)));
            if(top.containsKey(ladder)) {
                item.lore(" ").lore(C.color("&6Leaderboard "));
                for(int i = 0; i < 10; i++) {
                    if(i >= top.get(ladder).size())
                        break;

                    Map<String, Integer> kv = top.get(ladder);
                    String playerName = new ArrayList<>(kv.keySet()).get(i);
                    int position = i + 1, elo = kv.get(playerName);

                    item.lore("&6#" + position + ". &e" + playerName + " &7- &f" + elo);
                }
            }
            item.lore(C.color("&f&m------------"));

            inventory.setItem(index, item);
            index++;
        }

        player.openInventory(inventory);
    }
}
