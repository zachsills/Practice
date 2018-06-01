package net.practice.practice.spawn;

import net.practice.practice.Practice;
import net.practice.practice.util.chat.C;
import net.practice.practice.util.itemstack.I;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class SpawnHandler {

    public static void spawn(Player player) {
        player.teleport(Practice.getInstance().getSpawn());
        player.getInventory().setContents(getSpawnInventory());
    }

    private static ItemStack[] getSpawnInventory() {
        ItemStack[] items = new ItemStack[36];

        items[0] = new I(Material.IRON_SWORD).name(C.color("&eUnranked")).lore(C.color("&7Queue for an Unranked match."));
        items[1] = new I(Material.DIAMOND_SWORD).name(C.color("&6Ranked")).lore(C.color("&7Queue for an Ranked match."));
        items[2] = new I(Material.EGG).name(C.color("&7Last Queue [&equeueName&7]")).lore(C.color("&7Easily queue again and again and again and again :D."));
        items[4] = new I(Material.SKULL_ITEM).name(C.color("&cStats")).lore(C.color("&7Look at leaderboards and statistics."));
        items[6] = new I(Material.PUMPKIN_PIE).name(C.color("&bParty?")).lore(C.color("&7PARRRRRRR-TAY!"));
        items[7] = new I(Material.WATCH).name(C.color("&dSettings")).lore(C.color("&7Look at and spectate currently running matches."));
        items[8] = new I(Material.BOOK).name(C.color("&eKit Editor")).lore(C.color("&7Select kit and edit."));

        return items;
    }
}
