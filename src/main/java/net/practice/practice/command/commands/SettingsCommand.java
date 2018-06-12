package net.practice.practice.command.commands;

import net.practice.practice.game.player.Profile;
import net.practice.practice.util.command.Command;
import net.practice.practice.util.command.CommandArgs;

public class SettingsCommand {

    @Command(name = "settings", aliases = { "options" }, playerOnly = true, description = "Toggle your game settings.")
    public void onCommand(CommandArgs args) {
        Profile profile = Profile.getByPlayer(args.getPlayer());

        profile.openSettings();
    }
}
