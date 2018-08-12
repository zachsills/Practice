package land.nub.practice.command.commands;

import land.nub.practice.game.player.Profile;
import land.nub.practice.util.command.Command;
import land.nub.practice.util.command.CommandArgs;
import org.bukkit.entity.Player;

public class FlyCommand {

    @Command(name = "fly", playerOnly = true, permission = "practice.command.fly")
    public void onCommand(CommandArgs args) {
        Player player = args.getPlayer();
        Profile profile = Profile.getByPlayer(player);
        if(!profile.isInLobby()) {
            profile.sendMessage("&cThis command can only be executed in the lobby.");
            return;
        }

        player.setAllowFlight(!player.getAllowFlight());
        player.setFlying(player.getAllowFlight());

        profile.sendMessage("&eFlying is now " + (player.getAllowFlight() ? "&aenabled" : "&cdisabled") + "&e.");
    }
}
