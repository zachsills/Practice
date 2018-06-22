package net.practice.practice.inventory.item;

import net.practice.practice.util.chat.C;
import net.practice.practice.util.itemstack.I;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemStorage {

    // Lobby Items
    public static ItemStack LOBBY_RANKED = new I(Material.DIAMOND_SWORD).name(C.color("&6Ranked")).lore(C.color("&7Queue for an Ranked match."));
    public static ItemStack LOBBY_UNRANKED = new I(Material.IRON_SWORD).name(C.color("&eUnranked")).lore(C.color("&7Queue for an Unranked match."));
    public static ItemStack LOBBY_WATCH = new I(Material.WATCH).name(C.color("&6Settings")).lore(C.color("&7View and change your settings."));;
    public static ItemStack LOBBY_EDITOR = new I(Material.BOOK).name(C.color("&eKit Editor")).lore(C.color("&7Select kit and edit."));
    public static ItemStack LEAVE_QUEUE = new I(Material.INK_SACK).durability(1).name(C.color("&cLeave Queue")).lore(C.color("&7Click to leave your current queue."));

    // Spectator Items
    public static ItemStack SPECTATOR_INVENTORY = new I(Material.BOOK).name(C.color("&eView Inventory")).lore(C.color("&7Click a player to view their inventory."));
    public static ItemStack SPECTATOR_INFO = new I(Material.PAPER).name(C.color("&bView Match Info")).lore(C.color("&7Click to view match info."));
    public static ItemStack SPECTATOR_LEAVE = new I(Material.INK_SACK).durability(1).name(C.color("&cLeave Spectator Mode")).lore(C.color("&7Click to leave Spectator mode."));

    // Party Items
    public static ItemStack PARTY_LOBBY_CREATE = new I(Material.REDSTONE_TORCH_ON).name(C.color("&bParty")).lore(C.color("&7PARRRRRRR-TAY!"));
    public static ItemStack PARTY_LOBBY_LEAVE = new I(Material.NETHER_STAR).name(C.color("&cLeave Party")).lore(C.color("&7Leave your Party"));
    public static ItemStack PARTY_RANKED_DUOS = new I(LOBBY_RANKED).name(C.color("&e2v2 Ranked Queue")).lore(C.color("&7Queue for a 2v2 Ranked match."));
    public static ItemStack PARTY_UNRANKED_DUOS = new I(LOBBY_UNRANKED).name(C.color("&e2v2 Unranked Queue")).lore(C.color("&7Queue for a 2v2 Unranked match."));
    public static ItemStack PARTY_INFO = new I(Material.PAPER).name(C.color("&bView Party Info")).lore(C.color("&7Click to view info about your Partay."));
    public static ItemStack PARTY_EVENT = new I(Material.BEACON).name(C.color("&bStart a Party Event")).lore(C.color("&7Click to view info about your Partay."));
    public static ItemStack PARTY_FIGHT_OTHERS = new I(Material.SKULL_ITEM).name(C.color("&eFight Other Parties")).lore(C.color("&7Click to view all Parties."));
}
