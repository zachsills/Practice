package land.nub.practice.command.commands;

import land.nub.practice.game.player.Profile;
import land.nub.practice.spawn.SpawnHandler;
import land.nub.practice.util.chat.C;
import land.nub.practice.util.command.Command;
import land.nub.practice.util.command.CommandArgs;

public class SpawnCommand {

    @Command(name = "spawn", playerOnly = true, description = "Teleport to practice spawn.")
    public void onSpawn(CommandArgs args) {
        Profile profile = Profile.getByPlayer(args.getPlayer());
        if(profile.isInGame()) {
            args.getPlayer().sendMessage(C.color("&cYou cannot perform this command in a match."));
            return;
        }

        if(profile.isQueueing()) {
            profile.leaveQueue(true, true);
            return;
        }

        SpawnHandler.spawn(args.getPlayer());
        args.getPlayer().sendMessage(C.color("&f\u00BB &7Teleported to spawn."));
    }
}
