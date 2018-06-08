package net.practice.practice.inventory.item;

import net.practice.practice.util.chat.C;
import net.practice.practice.util.itemstack.I;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemStorage {

    public static ItemStack LEAVE_QUEUE = new I(Material.INK_SACK).durability(1).name(C.color("&cLeave Queue")).lore(C.color("&7Click to leave your current queue."));
}
