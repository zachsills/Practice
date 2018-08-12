package land.nub.practice.command.commands;

import land.nub.practice.game.player.Profile;
import land.nub.practice.game.player.data.InventorySnapshot;
import land.nub.practice.util.command.Command;
import land.nub.practice.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class InventoryCommand {

    @Command(name = "inventory", aliases = { "inv", "_" }, playerOnly = true, description = "View a player's inventory.")
    public void onInventory(CommandArgs args) {
        if(args.length() != 1) {
            args.getPlayer().sendMessage(ChatColor.RED + "/" + args.getLabel() + " <player>");
            return;
        }

        Player player = Bukkit.getPlayer(args.getArgs(0));
        if(player == null || !player.isOnline()) {
            args.getPlayer().sendMessage(ChatColor.RED + "The player '" + args.getArgs(0) + "' is not online.");
            return;
        }

        Profile profile = Profile.getByPlayer(player);
        if(profile.getRecentDuel() == null) {
            args.getPlayer().sendMessage(ChatColor.RED + "The player '" + args.getArgs(0) + "' doesn't have a recent inventory.");
            return;
        }

        InventorySnapshot recentSnapshot = profile.getRecentDuel().getSnapshot(player);

        recentSnapshot.open(args.getPlayer());
    }
}
