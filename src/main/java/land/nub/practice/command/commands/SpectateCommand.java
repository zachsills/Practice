package land.nub.practice.command.commands;

import land.nub.practice.game.player.Profile;
import land.nub.practice.game.player.data.ProfileState;
import land.nub.practice.util.RunnableShorthand;
import land.nub.practice.util.chat.C;
import land.nub.practice.util.command.Command;
import land.nub.practice.util.command.CommandArgs;
import land.nub.practice.util.command.Completer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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

    @Completer(name = "spectate", aliases = { "spec", "watch" })
    public List<String> onCompleter(CommandArgs args) {
        List<String> list = new ArrayList<>();
        for(Profile profile : Profile.getProfiles().values()) {
            if(!profile.isInGame())
                continue;

            list.add(profile.getName());
        }

        return list;
    }
}
