package net.practice.practice.spawn;

import net.practice.practice.Practice;
import net.practice.practice.game.player.Profile;
import net.practice.practice.game.player.data.ProfileState;
import net.practice.practice.util.InvUtils;
import net.practice.practice.util.chat.C;
import net.practice.practice.util.itemstack.I;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SpawnHandler {

    public static void spawn(Player player) {
        spawn(player, true);
    }

    public static void spawn(Player player, boolean tp) {
        InvUtils.clear(player);
        if(tp) {
            if(Practice.getInstance().getSpawn() != null)
                player.teleport(Practice.getInstance().getSpawn());
            else
                player.sendMessage(C.color("&cSpawn has not been set!"));

        }

        player.getInventory().setContents(getSpawnInventory(player));
        player.updateInventory();

        Profile profile = Profile.getByPlayer(player);
        profile.setProfileState(ProfileState.LOBBY);
        profile.setCurrentDuel(null);
    }

    public static ItemStack[] getSpawnInventory(Player player) {
        ItemStack[] items = new ItemStack[36];

        Profile profile = Profile.getByPlayer(player);

        items[0] = new I(Material.IRON_SWORD).name(C.color("&eUnranked")).lore(C.color("&7Queue for an Unranked match."));
        items[1] = new I(Material.DIAMOND_SWORD).name(C.color("&6Ranked")).lore(C.color("&7Queue for an Ranked match."));

        if(profile.getLastQueue() != null)
            items[2] = new I(Material.EGG).name(C.color("&7Last Queue [&equeueName&7]").replace("queueName", profile.getLastQueue().getLadder().getName())).lore(C.color("&7Easily queue again and again and again and again :D."));

        items[4] = new I(getSkull(player.getName())).name(C.color("&cStats")).lore(C.color("&7Look at leaderboards and statistics."));

        items[6] = new I(Material.PUMPKIN_PIE).name(C.color("&bParty?")).lore(C.color("&7PARRRRRRR-TAY!"));
        items[7] = new I(Material.WATCH).name(C.color("&dSettings")).lore(C.color("&7View and change your settings."));
        items[8] = new I(Material.BOOK).name(C.color("&eKit Editor")).lore(C.color("&7Select kit and edit."));

        return items;
    }

    public static ItemStack getSkull(String skullOwner) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal());
        SkullMeta skullMeta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
        skullMeta.setOwner(skullOwner);
        skull.setItemMeta(skullMeta);
        return skull;
    }
}
