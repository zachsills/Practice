package net.practice.practice.command.commands;

import net.practice.practice.game.player.Profile;
import net.practice.practice.inventory.inventories.StatsInv;
import net.practice.practice.util.chat.C;
import net.practice.practice.util.command.Command;
import net.practice.practice.util.command.CommandArgs;
import net.practice.practice.util.player.UUIDFetcher;
import org.bukkit.Bukkit;

import java.util.UUID;

public class StatsCommand {

    @Command(name = "stats", aliases = { "statistics" }, playerOnly = true, description = "View a player's stats.")
    public void onStats(CommandArgs args) {
        Profile profile;
        if(args.length() >= 1) {
            UUID uuid = UUIDFetcher.getUUID(args.getArgs(0));
            if(uuid != null) {
                profile = Profile.getByUuid(uuid);
                if(Bukkit.getOfflinePlayer(uuid) == null || !Bukkit.getOfflinePlayer(uuid).hasPlayedBefore()) {
                    args.getPlayer().sendMessage(C.color("&cThe player '" + args.getArgs(0) + "' is not a player."));
                    return;
                }
            } else {
                args.getPlayer().sendMessage(C.color("&cThe player '" + args.getArgs(0) + "' is not a player."));
                return;
            }
        } else
            profile = Profile.getByPlayer(args.getPlayer());

        StatsInv.openInventory(args.getPlayer(), profile);
    }

    /*@Command(name = "stats", aliases = { "statistics" }, playerOnly = true, description = "View a Player's stats.")
    public void onStats(CommandArgs args) {
        Profile profile;
        if(args.length() >= 1) {
            UUID uuid = UUIDFetcher.getUUID(args.getArgs(0));
            if(uuid != null) {
                profile = Profile.getByUuid(uuid);
                if(Bukkit.getOfflinePlayer(uuid) == null || !Bukkit.getOfflinePlayer(uuid).hasPlayedBefore()) {
                    args.getPlayer().sendMessage(C.color("&cThe player '" + args.getArgs(0) + "' is not a player."));
                    return;
                }
            } else {
                args.getPlayer().sendMessage(C.color("&cThe player '" + args.getArgs(0) + "' is not a player."));
                return;
            }
        } else
            profile = Profile.getByPlayer(args.getPlayer());

        OfflinePlayer player = Bukkit.getOfflinePlayer(profile.getUuid());
        args.getPlayer().sendMessage(C.color("&e" + player.getName() + "'s Statistics"));
        args.getPlayer().sendMessage(C.color("&6&lWins: "));
        args.getPlayer().sendMessage(C.color(" &aRanked &7- &e" + profile.getRankedWins()));
        args.getPlayer().sendMessage(C.color(" &aUnranked &7- &e" + profile.getUnrankedWins()));
        args.getPlayer().sendMessage(C.color("&6&lLosses: "));
        args.getPlayer().sendMessage(C.color(" &aRanked &7- &e" + profile.getRankedLosses()));
        args.getPlayer().sendMessage(C.color(" &aUnranked &7- &e" + profile.getUnrankedLosses()));
        new JsonMessage().append(ChatColor.YELLOW + "Click to view ELO statistics.").setClickAsExecuteCmd("/elo " + player.getName()).save().send(args.getPlayer());
    }

    @Command(name = "elo", aliases = { "elo" }, playerOnly = true, description = "View a Player's stats.")
    public void onElo(CommandArgs args) {
        Profile profile;
        if(args.length() >= 1) {
            UUID uuid = UUIDFetcher.getUUID(args.getArgs(0));
            if(uuid != null) {
                profile = Profile.getByUuid(uuid);
                if(Bukkit.getOfflinePlayer(uuid) == null || !Bukkit.getOfflinePlayer(uuid).hasPlayedBefore()) {
                    args.getPlayer().sendMessage(C.color("&cThe player '" + args.getArgs(0) + "' is not a player."));
                    return;
                }
            } else {
                args.getPlayer().sendMessage(C.color("&cThe player '" + args.getArgs(0) + "' is not a player."));
                return;
            }
        } else
            profile = Profile.getByPlayer(args.getPlayer());

        OfflinePlayer player = Bukkit.getOfflinePlayer(profile.getUuid());
        args.getPlayer().sendMessage(C.color("&e" + player.getName() + "'s Elo Statistics: "));
        for(Ladder ladder : Ladder.getLadders().values())
            args.getPlayer().sendMessage(C.color("&a" + ladder.getName() + " &7- &6" + profile.getElo(ladder)));
    }*/
}
