package net.practice.practice.inventory.item;

import net.practice.practice.util.chat.C;
import net.practice.practice.util.itemstack.I;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemStorage {

    // Lobby Items
    public static ItemStack LEAVE_QUEUE = new I(Material.INK_SACK).durability(1).name(C.color("&cLeave Queue")).lore(C.color("&7Click to leave your current queue."));


    // Spectator Items
    public static ItemStack SPECTATOR_INVENTORY = new I(Material.BOOK).durability(1).name(C.color("&eView Inventory")).lore(C.color("&7Click a player to view their inventory."));
    public static ItemStack SPECTATOR_INFO = new I(Material.PAPER).durability(1).name(C.color("&bView Match Info")).lore(C.color("&7Click to view match info."));
    public static ItemStack SPECTATOR_LEAVE = new I(Material.INK_SACK).durability(1).name(C.color("&cLeave Spectator")).lore(C.color("&7Click to leave Spectator mode."));
}
