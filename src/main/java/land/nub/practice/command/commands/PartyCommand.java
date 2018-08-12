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

        Player target = Bukkit.getPlayer(args.getArgs(0));
        if(target == null) {
            args.getPlayer().sendMessage(C.color("&cThe player '" + args.getArgs(0) + "' is not online."));
            return;
        }

        Profile profile = Profile.getByPlayer(args.getPlayer());
        if(!profile.isInParty()) {
            profile.sendMessage("&cYou cannot invite someone to a party without creating a party first.");
            return;
        }

        Party profileParty = profile.getParty();
//        if(party.getSize() > LIMITE) {
//
//            return;
//        }
        if(!profileParty.getLeader().equals(profile.getUuid())) {
            profile.sendMessage("&cYou must be the party leader to invite players.");
            return;
        }

        if(profileParty.getInvites().contains(target)) {
            profile.sendMessage("&cYou have already invited this player to your party.");
            return;
        }

        profileParty.getInvites().add(target);
        profile.sendMessage("&aYou have invited " + target.getName() + " to the party.");

        new JsonMessage()
                .append(C.color("&eYou have been invited to " + profileParty.getLeaderName() + "'s party. ")).save()
                .append(C.color("&a[Accept]")).setHoverAsTooltip(C.color("&7Click to join the party.")).setClickAsExecuteCmd("/party join " + profileParty.getLeaderName()).save()
                .append(" ").save()
                .append(C.color("&b[Info]")).setHoverAsTooltip(C.color("&7View info about this party.")).setClickAsExecuteCmd("/party info " + profileParty.getLeaderName()).save()
                .send(target);
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

        Player target = Bukkit.getPlayer(args.getArgs(0));
        if(target == null) {
            args.getPlayer().sendMessage(C.color("&cThe player '" + args.getArgs(0) + "' is not online."));
            return;
        }

        Profile personJoining = Profile.getByPlayer(args.getPlayer());
        Profile requester = Profile.getByPlayer(target);

        if(!requester.isInParty()) {
            personJoining.sendMessage("&cThis player is not in a party.");
            return;
        }

        if(!requester.getParty().getInvites().contains(args.getPlayer())) {
            personJoining.sendMessage("&cYou must be invited to this party first.");
            return;
        }

        if (personJoining.isInParty()) {
            onPartyLeave(args);
        }

        Party requesterParty = requester.getParty();
        personJoining.joinParty(requesterParty);

        requesterParty.getInvites().remove(personJoining.getPlayer());
        requesterParty.getPlayers().add(personJoining.getPlayer());
        requesterParty.sendMessage("&b" + personJoining.getPlayer().getName() + " &ehas joined the party.");
    }

    @Command(name = "party.leave", aliases = { "p.leave" }, playerOnly = true, description = "Party commands.")
    public void onPartyLeave(CommandArgs args) {
        Profile profile = Profile.getByPlayer(args.getPlayer());
        if(!profile.isInParty()) {
            profile.sendMessage("&cYou must have a party to perform this action.");
            return;
        }

        profile.handleLeaveParty();
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
