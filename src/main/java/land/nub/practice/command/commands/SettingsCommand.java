package land.nub.practice.command.commands;

import land.nub.practice.game.player.Profile;
import land.nub.practice.util.command.Command;
import land.nub.practice.util.command.CommandArgs;

public class SettingsCommand {

    @Command(name = "settings", aliases = { "options" }, playerOnly = true, description = "Toggle your game settings.")
    public void onCommand(CommandArgs args) {
        Profile profile = Profile.getByPlayer(args.getPlayer());

        profile.openSettings();
    }
}
