package land.nub.practice.command.commands;

import land.nub.practice.game.duel.DuelRequest;
import land.nub.practice.game.player.Profile;
import land.nub.practice.game.player.data.ProfileSetting;
import land.nub.practice.game.player.data.ProfileState;
import land.nub.practice.inventory.inventories.RequestInv;
import land.nub.practice.util.chat.C;
import land.nub.practice.util.command.Command;
import land.nub.practice.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DuelCommand {

    @Command(name = "duel", playerOnly = true, description = "Send a duel request to a player.")
    public void onDuel(CommandArgs args) {
        if(args.length() < 1) {
            args.getPlayer().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }

        if(args.getArgs(0).equalsIgnoreCase(args.getPlayer().getName())) {
            args.getPlayer().sendMessage(ChatColor.RED + "You cannot duel yourself.");
            return;
        }


        Profile profile = Profile.getByPlayer(args.getPlayer());

        if(profile.getState() != ProfileState.LOBBY) {
            args.getPlayer().sendMessage(ChatColor.RED + "You cannot perform this action in your current state.");
            return;
        }

        Player player = Bukkit.getPlayer(args.getArgs(0));
        if(player == null || !player.isOnline()) {
            args.getPlayer().sendMessage(ChatColor.RED + "The player '" + args.getArgs(0) + "' is not online.");
            return;
        }

        if(!profile.isInParty()) {
            Profile targetProfile = Profile.getByPlayer(player);
            if(!(boolean) targetProfile.getSetting(ProfileSetting.DUEL_REQUESTS)) {
                args.getPlayer().sendMessage(C.color("&cThat player is currently not accepting any duels."));
                return;
            }

            if(targetProfile.isInGame()) {
                args.getPlayer().sendMessage(C.color("&cThat player is currently in a duel."));
                return;
            }

            if(targetProfile.getDuelRequests().containsKey(args.getPlayer().getName())) {
                args.getPlayer().sendMessage(C.color("&cYou have already sent this player a request."));
                return;
            }

            RequestInv.openInventory(args.getPlayer(), player);
        } else {
            Profile targetProfile = Profile.getByPlayer(player);
            if(!targetProfile.isInParty()) {
                args.getPlayer().sendMessage(C.color("&cThat party is currently not in a party."));
                return;
            }

            if(targetProfile.getParty().isInGame()) {
                args.getPlayer().sendMessage(C.color("&cThat party is currently occupied."));
                return;
            }

            if(targetProfile.getParty().getRequests().containsKey(profile.getParty())) {
                args.getPlayer().sendMessage(C.color("&cYou have already sent this party a duel request."));
                return;
            }

            if (profile.getParty().getAllPlayers().size() < 2 || targetProfile.getParty().getAllPlayers().size() < 2) {
                args.getPlayer().sendMessage(C.color("&cYou need at least 2 players to duel another party!"));
                return;
            }

            RequestInv.openInventory(args.getPlayer(), player);
        }
    }

    @Command(name = "accept", aliases = { "acceptrequest" }, playerOnly = true, description = "Accept a duel request from a player.")
    public void onAccept(CommandArgs args) {
        if(args.length() < 1) {
            args.getPlayer().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }

        Profile profile = Profile.getByPlayer(args.getPlayer());
        if(profile.getState() != ProfileState.LOBBY) {
            args.getPlayer().sendMessage(ChatColor.RED + "You cannot perform this action in your current state.");
            return;
        }

        Player player = Bukkit.getPlayer(args.getArgs(0));
        if(player == null || !player.isOnline()) {
            args.getPlayer().sendMessage(ChatColor.RED + "The player '" + args.getArgs(0) + "' is not online.");
            return;
        }

        if (!profile.isInParty()) {
            Profile targetProfile = Profile.getByPlayer(player);
            if(targetProfile.isInGame()) {
                args.getPlayer().sendMessage(C.color("&cThat player is currently in a duel."));
                return;
            }

            if(!targetProfile.getDuelRequests().containsKey(args.getPlayer().getName())) {
                args.getPlayer().sendMessage(C.color("&cYou have not received a duel request from that player."));
                return;
            }

            DuelRequest request = targetProfile.getDuelRequests().get(args.getPlayer().getName());
            request.accept();
        } else {
            Profile targetProfile = Profile.getByPlayer(player);
            if (!targetProfile.isInParty()) {
                args.getPlayer().sendMessage(C.color("&cThat party is currently not in a party."));
                return;
            }

            if (targetProfile.getParty().isInGame()) {
                args.getPlayer().sendMessage(C.color("&cThat party is currently occupied."));
                return;
            }

            if (!targetProfile.getParty().getRequests().containsKey(profile.getParty())) {
                args.getPlayer().sendMessage(C.color("&cYou have not received a duel request from that player."));
                return;
            }

            targetProfile.getParty().getRequests().get(profile.getParty()).accept();
        }
    }

    @Command(name = "deny", aliases = { "denyrequest" }, playerOnly = true, description = "Deny a duel request from a player.")
    public void onDeny(CommandArgs args) {
        if(args.length() < 1) {
            args.getPlayer().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }

        Profile profile = Profile.getByPlayer(args.getPlayer());
        if(profile.getState() != ProfileState.LOBBY) {
            args.getPlayer().sendMessage(ChatColor.RED + "You cannot perform this action in your current state.");
            return;
        }

        Player player = Bukkit.getPlayer(args.getArgs(0));
        if(player == null || !player.isOnline()) {
            args.getPlayer().sendMessage(ChatColor.RED + "The player '" + args.getArgs(0) + "' is not online.");
            return;
        }

        if (!profile.isInParty()) {
            Profile targetProfile = Profile.getByPlayer(player);
            if(!targetProfile.getDuelRequests().containsKey(args.getPlayer().getName())) {
                args.getPlayer().sendMessage(C.color("&cYou have not received a duel request from that player."));
                return;
            }

            DuelRequest request = targetProfile.getDuelRequests().get(args.getPlayer().getName());

            request.deny();

            args.getPlayer().sendMessage(C.color("&aYou have denied " + player.getName() + "'s request."));
        } else {
            Profile targetProfile = Profile.getByPlayer(player);
            if (!targetProfile.isInParty()) {
                args.getPlayer().sendMessage(C.color("&cThat party is currently not in a party."));
                return;
            }

            if(!targetProfile.getParty().getRequests().containsKey(profile.getParty())) {
                args.getPlayer().sendMessage(C.color("&cYou have not received a duel request from that player."));
                return;
            }

            targetProfile.getParty().getRequests().get(profile.getParty()).deny();

            args.getPlayer().sendMessage(C.color("&aYou have denied " + player.getName() + "'s party's request."));
        }
    }
}
