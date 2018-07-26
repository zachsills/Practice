package net.practice.practice.command.commands;

import net.practice.practice.game.player.Profile;
import net.practice.practice.game.player.data.ProfileState;
import net.practice.practice.util.RunnableShorthand;
import net.practice.practice.util.chat.C;
import net.practice.practice.util.command.Command;
import net.practice.practice.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class SpectateCommand {

    @Command(name = "spectate", aliases = { "spec", "watch" }, playerOnly = true, description = "Spectate other player's matches.")
    public void onSpectate(CommandArgs args) {
        if(args.length() != 1) {
            args.getPlayer().sendMessage(C.color("&cUsage: /" + args.getLabel() + " <player>"));
            return;
        }

        if(args.getArgs(0).equalsIgnoreCase(args.getPlayer().getName())) {
            args.getPlayer().sendMessage(ChatColor.RED + "You cannot spectate yourself.");
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


        Profile targetProfile = Profile.getByPlayer(player);
        if(!targetProfile.isInGame()) {
            args.getPlayer().sendMessage(ChatColor.RED + "The player '" + args.getArgs(0) + "' is not in a match.");
            return;
        }

        profile.setSpectating(targetProfile.getCurrentDuel());
        targetProfile.getCurrentDuel().getSpectators().add(profile);

        profile.getPlayer().teleport(player.getLocation());

        RunnableShorthand.runNextTick(() -> {
            profile.getPlayer().setAllowFlight(true);
            profile.getPlayer().setFlying(true);
            profile.getPlayer().setGameMode(GameMode.CREATIVE);
        });
    }
}
