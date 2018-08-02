package net.practice.practice.spawn;

import net.practice.practice.Practice;
import net.practice.practice.game.duel.Duel;
import net.practice.practice.game.duel.DuelType;
import net.practice.practice.game.duel.type.SoloDuel;
import net.practice.practice.game.player.Profile;
import net.practice.practice.game.player.data.ProfileState;
import net.practice.practice.inventory.item.ItemStorage;
import net.practice.practice.util.InvUtils;
import net.practice.practice.util.chat.C;
import net.practice.practice.util.itemstack.I;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
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

        player.setGameMode(GameMode.SURVIVAL);
        player.setFlying(false);
        player.setAllowFlight(false);

        player.setLevel(0);
        player.setExp(0.0F);

        ((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) -1);

        Profile profile = Profile.getByPlayer(player);

        if(profile.isInParty())
            PartyHandler.spawn(player, profile.getParty().getLeader().equals(profile.getUuid()));
        else
            player.getInventory().setContents(getSpawnInventory(player));
        player.updateInventory();

        if(profile.isQueueing())
            profile.leaveQueue(false);

        profile.setState(ProfileState.LOBBY);
        profile.setCurrentDuel(null);

        Bukkit.getOnlinePlayers().forEach(other -> {
            other.showPlayer(player);
            player.showPlayer(other);
        });
    }

    public static ItemStack[] getSpawnInventory(Player player) {
        Profile profile = Profile.getByPlayer(player);

        ItemStack[] items = new ItemStack[36];

        items[0] = ItemStorage.LOBBY_UNRANKED;
        items[1] = ItemStorage.LOBBY_RANKED;

        if(profile.getRecentDuel() != null) {
            Duel duel = profile.getRecentDuel();
            if(duel.getType() == DuelType.ONE_VS_ONE && Math.abs(System.currentTimeMillis() - duel.getEndTime()) < 1000 * 20) {
                SoloDuel soloDuel = (SoloDuel) duel;
                Player rematcher = soloDuel.getPlayerOne() == player ? soloDuel.getPlayerTwo() : soloDuel.getPlayerOne();
                if(rematcher != null && rematcher.isOnline())
                    items[2] = new I(Material.INK_SACK).durability(10).name(C.color("&bRematch playerName")
                            .replace("playerName", rematcher.getName())).lore(C.color("&7Easily send a rematch request to your last opponent."));
            }
        }

        items[3] = new I(getSkull(player.getName())).name(C.color("&cStats")).lore(C.color("&7Look at leaderboards and statistics."));

        items[5] = ItemStorage.PARTY_LOBBY_CREATE;

        items[7] = ItemStorage.LOBBY_COSMETICS;
        items[8] = ItemStorage.LOBBY_EDITOR;

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
