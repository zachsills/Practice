package net.practice.practice.command.commands;

import net.practice.practice.game.duel.DuelRequest;
import net.practice.practice.game.player.Profile;
import net.practice.practice.inventory.inventories.RequestInv;
import net.practice.practice.util.chat.C;
import net.practice.practice.util.command.Command;
import net.practice.practice.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DuelCommand {

    @Command(name = "duel", aliases = { "request" }, playerOnly = true, description = "Send a duel request to a player.")
    public void onDuel(CommandArgs args) {
        if(args.length() < 1) {
            args.getPlayer().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }

        if(args.getArgs(0).equalsIgnoreCase(args.getPlayer().getName())) {
            args.getPlayer().sendMessage(ChatColor.RED + "You cannot duel yourself.");
            return;
        }

        Player player = Bukkit.getPlayer(args.getArgs(0));
        if(player == null || !player.isOnline()) {
            args.getPlayer().sendMessage(ChatColor.RED + "The player '" + args.getArgs(0) + "' is not online.");
            return;
        }

        Profile profile = Profile.getByPlayer(player);
        if(profile.getDuelRequests().containsKey(args.getPlayer().getName())) {
            args.getPlayer().sendMessage(C.color("&cYou have already sent this player a request."));
            return;
        }

        RequestInv.openInventory(args.getPlayer(), player);
    }

    @Command(name = "accept", aliases = { "acceptrequest" }, playerOnly = true, description = "Accapt a duel request from a player.")
    public void onAccept(CommandArgs args) {
        if(args.length() < 1) {
            args.getPlayer().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }

        Player player = Bukkit.getPlayer(args.getArgs(0));
        if(player == null || !player.isOnline()) {
            args.getPlayer().sendMessage(ChatColor.RED + "The player '" + args.getArgs(0) + "' is not online.");
            return;
        }

        Profile profile = Profile.getByPlayer(player);
        if(!profile.getDuelRequests().containsKey(args.getPlayer().getName())) {
            args.getPlayer().sendMessage(C.color("&cYou have not received a duel request from that player."));
            return;
        }

        DuelRequest request = profile.getDuelRequests().get(args.getPlayer().getName());

        request.accept();
    }

    @Command(name = "deny", aliases = { "denyrequest" }, playerOnly = true, description = "Deny a duel request from a player.")
    public void onDeny(CommandArgs args) {
        if(args.length() < 1) {
            args.getPlayer().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }

        Player player = Bukkit.getPlayer(args.getArgs(0));
        if(player == null || !player.isOnline()) {
            args.getPlayer().sendMessage(ChatColor.RED + "The player '" + args.getArgs(0) + "' is not online.");
            return;
        }

        Profile profile = Profile.getByPlayer(player);
        if(!profile.getDuelRequests().containsKey(args.getPlayer().getName())) {
            args.getPlayer().sendMessage(C.color("&cYou have not received a duel request from that player."));
            return;
        }

        DuelRequest request = profile.getDuelRequests().get(args.getPlayer().getName());

        request.deny();

        args.getPlayer().sendMessage(C.color("&aYou have denied " + player.getName() + "'s request."));
    }
}
