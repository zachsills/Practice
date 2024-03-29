package land.nub.practice.command.commands;

import land.nub.practice.game.player.Profile;
import land.nub.practice.inventory.inventories.StatsInv;
import land.nub.practice.util.chat.C;
import land.nub.practice.util.command.Command;
import land.nub.practice.util.command.CommandArgs;
import land.nub.practice.util.player.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class StatsCommand {

    @Command(name = "stats", aliases = { "statistics" }, playerOnly = true, description = "View a player's stats.")
    public void onStats(CommandArgs args) {
        Profile profile;
        if(args.length() >= 1) {
            UUID uuid = UUIDFetcher.getUUID(args.getArgs(0));
            if(uuid != null) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                if(player.isOnline() || player.hasPlayedBefore()) {
                    profile = Profile.getByUuid(uuid);
                } else {
                    args.getPlayer().sendMessage(C.color("&cThe player '" + args.getArgs(0) + "' has never logged on."));
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
