package net.practice.practice.command.commands;

import net.practice.practice.game.party.Party;
import net.practice.practice.game.party.PartyManager;
import net.practice.practice.game.player.Profile;
import net.practice.practice.spawn.PartyHandler;
import net.practice.practice.spawn.SpawnHandler;
import net.practice.practice.util.chat.C;
import net.practice.practice.util.chat.JsonMessage;
import net.practice.practice.util.command.Command;
import net.practice.practice.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PartyCommand {

    @Command(name = "party", aliases = { "p" }, playerOnly = true, description = "Party commands.")
    public void onParty(CommandArgs args) {
        sendHelp(args.getPlayer());
    }

    @Command(name = "party.invite", aliases = { "p.invite" }, playerOnly = true, description = "Party commands.")
    public void onPartyInvite(CommandArgs args) {
        if(args.length() < 1) {
            args.getPlayer().sendMessage(C.color("&cUsage: /party invite <player>"));
            return;
        }

        Player player = Bukkit.getPlayer(args.getArgs(0));
        if(player == null) {
            args.getPlayer().sendMessage(C.color("&cThe player '" + args.getArgs(0) + "' is not online."));
            return;
        }

        Profile profile = Profile.getByPlayer(args.getPlayer());
        if(!profile.isInParty()) {
            profile.sendMessage("&cYou cannot invite someone to a party without creating a party first.");
            return;
        }

        Party party = profile.getParty();
//        if(party.getSize() > LIMITE) {
//
//            return;
//        }
        if(!party.getLeader().equals(profile.getUuid())) {
            profile.sendMessage("&cYou must be the party leader to invite players.");
            return;
        }

        if(party.getInvites().contains(player)) {
            profile.sendMessage("&cYou have already invited this player to your party.");
            return;
        }

        party.getInvites().add(player);
        profile.sendMessage("&aYou have invited " + player.getName() + " to the party.");

        new JsonMessage()
                .append(C.color("&eYou have been invited to " + party.getLeaderName() + "'s party. ")).save()
                .append(C.color("&a[Accept]")).setHoverAsTooltip(C.color("&7Click to join the party.")).setClickAsExecuteCmd("/party join " + party.getLeaderName()).save()
                .append(" ").save()
                .append(C.color("&b[Info]")).setHoverAsTooltip(C.color("&7View info about this party.")).setClickAsExecuteCmd("/party info " + party.getLeaderName()).save()
                .send(player);
    }

    @Command(name = "party.uninvite", aliases = { "p.uninvite" }, playerOnly = true, description = "Party commands.")
    public void onPartyUninvite(CommandArgs args) {
        if(args.length() < 1) {
            args.getPlayer().sendMessage(C.color("&cUsage: /party uninvite <player>"));
            return;
        }

        Player player = Bukkit.getPlayer(args.getArgs(0));
        if(player == null) {
            args.getPlayer().sendMessage(C.color("&cThe player '" + args.getArgs(0) + "' is not online."));
            return;
        }

        Profile profile = Profile.getByPlayer(args.getPlayer());
        if(!profile.isInParty()) {
            profile.sendMessage("&cYou cannot invite someone to a party without creating a party first.");
            return;
        }

        Party party = profile.getParty();
//        if(party.getSize() > LIMITE) {
//
//            return;
//        }
        if(!party.getLeader().equals(profile.getUuid())) {
            profile.sendMessage("&cYou must be the party leader to invite players.");
            return;
        }

        boolean hasInvitee = party.getPlayers().stream().anyMatch(invitee -> invitee.getName().equals(player.getName()));
        if(!hasInvitee) {
            profile.sendMessage("&cYou have not invited '" + player.getName() + "' to the party.");
            return;
        }

        party.getInvites().remove(player);
        profile.sendMessage("&aYou have officially uninvited " + player.getName() + " to the party.");
    }

    @Command(name = "party.join", aliases = { "p.join" }, playerOnly = true, description = "Party commands.")
    public void onPartyJoin(CommandArgs args) {
        if(args.length() < 1) {
            args.getPlayer().sendMessage(C.color("&cUsage: /party join <player>"));
            return;
        }

        Player player = Bukkit.getPlayer(args.getArgs(0));
        if(player == null) {
            args.getPlayer().sendMessage(C.color("&cThe player '" + args.getArgs(0) + "' is not online."));
            return;
        }

        Profile other = Profile.getByPlayer(args.getPlayer());
        if(other.isInParty()) {
            other.sendMessage("&cYou must leave your current party first.");
            return;
        }

        Profile profile = Profile.getByPlayer(player);
        if(!profile.isInParty()) {
            profile.sendMessage("&cThis player is not in a party.");
            return;
        }

        Party party = profile.getParty();
//        if(party.getSize() > LIMITE) {
//
//            return;
//        }
        if(!party.getInvites().contains(args.getPlayer())) {
            profile.sendMessage("&cYou must be invited to this party first.");
            return;
        }

        party.sendMessage("&b" + args.getPlayer().getName() + " &ehas joined your party.");
        party.getInvites().remove(other.getPlayer());
        party.getPlayers().add(other.getPlayer());

        other.joinParty(party);
    }

    @Command(name = "party.leave", aliases = { "p.leave" }, playerOnly = true, description = "Party commands.")
    public void onPartyLeave(CommandArgs args) {
        Profile profile = Profile.getByPlayer(args.getPlayer());
        if(!profile.isInParty()) {
            profile.sendMessage("&cYou must have a party to perform this action.");
            return;
        }

        Party party = profile.getParty();
        if(party.getCurrentQueue() != null)
            party.leaveQueue();

        if(party.getLeader().equals(profile.getUuid())) {
            if(party.getPlayers().size() > 0) {
                UUID newLeader = party.getPlayers().get(0).getUniqueId();
                party.getPlayers().remove(0);

                Bukkit.getPlayer(newLeader).sendMessage(C.color("&aYou have been made the leader of the party."));
                party.sendMessage("&b" + Bukkit.getPlayer(newLeader).getName() + " &ehas been made the leader of your party.");

                party.setLeader(newLeader);
                PartyHandler.spawn(Bukkit.getPlayer(newLeader), true);
            } else {
                PartyManager.removeParty(party);
            }
            profile.leaveParty();
            party.sendMessage("&b" + args.getPlayer().getName() + " &ehas left the party.");
            args.getPlayer().sendMessage(C.color("&aYou have left your party."));
            return;
        }

        party.getPlayers().remove(profile.getPlayer());
        profile.leaveParty();

        party.sendMessage("&b" + args.getPlayer().getName() + " &ehas left the party.");
        args.getPlayer().sendMessage(C.color("&aYou have left your party."));
    }

    @Command(name = "party.disband", aliases = { "p.disband" }, playerOnly = true, description = "Party commands.")
    public void onPartyDisband(CommandArgs args) {
        Profile profile = Profile.getByPlayer(args.getPlayer());
        if(!profile.isInParty()) {
            profile.sendMessage("&cYou must have a party to perform this action.");
            return;
        }

        Party party = profile.getParty();
        if(!party.getLeader().equals(profile.getUuid())) {
            profile.sendMessage("&cYou are not a leader of this party.");
            return;
        }

        party.sendMessage("&cThe party was disbanded.");
        profile.leaveParty();
        for(Player player : party.getPlayers()) {
            Profile.getByPlayer(player).leaveParty();
            SpawnHandler.spawn(player);
        }

        PartyManager.removeParty(party);
    }

    @Command(name = "party.info", aliases = { "p.info" }, playerOnly = true, description = "Party commands.")
    public void onPartyInfo(CommandArgs args) {
        if(args.length() < 1) {
            args.getPlayer().sendMessage(C.color("&cUsage: /party info <player>"));
            return;
        }

        Player player = Bukkit.getPlayer(args.getArgs(0));
        if(player == null) {
            args.getPlayer().sendMessage(C.color("&cThe player '" + args.getArgs(0) + "' is not online."));
            return;
        }

        Profile profile = Profile.getByPlayer(player);
        if(!profile.isInParty()) {
            profile.sendMessage("&cThis player is not in a party.");
            return;
        }

        Party party = profile.getParty();

        Profile.getByPlayer(args.getPlayer()).sendPartyInfo(party);
    }

    private void sendHelp(Player player) {
        player.sendMessage(C.color("&e&lParty Help"));
        player.sendMessage(C.color("&e/party invite <player>"));
        player.sendMessage(C.color("&e/party uninvite <player>"));
        player.sendMessage(C.color("&e/party join <player>"));
        player.sendMessage(C.color("&e/party leave"));
    }
}
